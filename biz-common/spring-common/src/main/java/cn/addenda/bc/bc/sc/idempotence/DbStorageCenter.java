package cn.addenda.bc.bc.sc.idempotence;

import cn.addenda.bc.bc.jc.concurrent.SimpleNamedThreadFactory;
import cn.addenda.bc.bc.jc.util.ConnectionUtils;
import cn.addenda.bc.bc.jc.util.DateUtils;
import cn.addenda.bc.bc.jc.util.JacksonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2023/8/3 14:53
 */
@Slf4j
public class DbStorageCenter implements StorageCenter, InitializingBean, ApplicationListener<ContextClosedEvent> {

    private final DataSource dataSource;

    private final SQLExceptionTranslator exceptionTranslator;

    public DbStorageCenter(DataSource dataSource) {
        this.dataSource = dataSource;
        this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
    }

    private static final String SAVE_SQL =
        "insert into t_idempotence_storage_center "
            + "set `namespace` = ?, `prefix` = ?, `key` = ?, `consume_mode` = ?, `x_id` = ?, `consume_status` = ?, `expire_time` = date_add(now(), interval ? second) ";

    @Override
    @SneakyThrows
    public ConsumeStatus getSet(IdempotenceParamWrapper param, ConsumeStatus consumeStatus) {
        while (true) {
            Connection connection = null;
            boolean originalAutoCommit = false;
            try {
                connection = dataSource.getConnection();
                originalAutoCommit = ConnectionUtils.setAutoCommitFalse(connection);
                try (PreparedStatement ps = connection.prepareStatement(SAVE_SQL)) {
                    ps.setString(1, param.getNamespace());
                    ps.setString(2, param.getPrefix());
                    ps.setString(3, param.getKey());
                    ps.setString(4, param.getConsumeMode().name());
                    ps.setString(5, param.getXId());
                    ps.setObject(6, consumeStatus.name());
                    ps.setLong(7, param.getTtlSecs());
                    ps.executeUpdate();
                }
                connection.commit();
                return null;
            } catch (SQLException e) {
                if (connection != null) {
                    connection.rollback();
                }
                DataAccessException translate = exceptionTranslator.translate(e.getMessage() + "\n", null, e);
                if (!(translate instanceof DuplicateKeyException)) {
                    throw e;
                }
                StorageCenterEntity old = doGet(param, true, connection);
                if (old != null) {
                    return old.getConsumeStatus();
                }
            } finally {
                ConnectionUtils.closeAndResetAutoCommit(connection, originalAutoCommit);
            }
        }
    }

    private static final String GET_SQL1 =
        "select `id`, `namespace`, `prefix`, `key`, `consume_mode`, `x_id`, `consume_status`, `expire_time` from t_idempotence_storage_center "
            + "where `namespace` = ? and `prefix` = ? and `key` = ?";

    private static final String GET_SQL2 =
        "select `id`, `namespace`, `prefix`, `key`, `consume_mode`, `x_id`, `consume_status`, `expire_time` from t_idempotence_storage_center "
            + "where `namespace` = ? and `prefix` = ? and `key` = ? and `x_id` = ?";

    @SneakyThrows
    private StorageCenterEntity doGet(IdempotenceParamWrapper param, boolean includeOther, Connection connection) {
        String getSql = includeOther ? GET_SQL1 : GET_SQL2;
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSql)) {
            preparedStatement.setString(1, param.getNamespace());
            preparedStatement.setString(2, param.getPrefix());
            preparedStatement.setString(3, param.getKey());
            if (!includeOther) {
                preparedStatement.setString(4, param.getXId());
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<StorageCenterEntity> storageCenterEntityList = assembleEntityList(resultSet);
            if (storageCenterEntityList.isEmpty()) {
                return null;
            } else if (storageCenterEntityList.size() > 1) {
                String msg = String.format("Get StorageCenterEntity from [%s] error. Result has multi records.", param);
                throw new IdempotenceException(msg);
            } else {
                return storageCenterEntityList.get(0);
            }
        }
    }

    private static final String UPDATE_SQL1 =
        "update t_idempotence_storage_center "
            + "set `consume_status` = ?, `expire_time` = date_add(now(), interval ? second), `x_id` = ? where `namespace` = ? and `prefix` = ? and `key` = ? and `consume_status` = ? ";

    private static final String UPDATE_SQL2 =
        "update t_idempotence_storage_center "
            + "set `consume_status` = ?, `expire_time` = date_add(now(), interval ? second), `x_id` = ? where `namespace` = ? and `prefix` = ? and `key` = ? and `consume_status` = ? and `x_id` = ? ";

    @Override
    @SneakyThrows
    public boolean casStatus(IdempotenceParamWrapper param, ConsumeStatus expected, ConsumeStatus consumeStatus, boolean casOther) {
        String updateSql = casOther ? UPDATE_SQL1 : UPDATE_SQL2;
        boolean result;
        Connection connection = null;
        boolean originalAutoCommit = false;
        try {
            connection = dataSource.getConnection();
            originalAutoCommit = ConnectionUtils.setAutoCommitFalse(connection);
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setString(1, consumeStatus.name());
                ps.setLong(2, param.getTtlSecs());
                ps.setString(3, param.getXId());
                ps.setString(4, param.getNamespace());
                ps.setString(5, param.getPrefix());
                ps.setString(6, param.getKey());
                ps.setString(7, expected.name());
                if (!casOther) {
                    ps.setString(8, param.getXId());
                }
                result = ps.executeUpdate() == 1;
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            ConnectionUtils.closeAndResetAutoCommit(connection, originalAutoCommit);
        }
    }

    @Override
    public void saveExceptionLog(IdempotenceParamWrapper param, IdempotenceScenario scenario, Object[] arguments, ConsumeStage consumeStage, String message, Throwable throwable) {
        String argsJson = JacksonUtils.objectToString(arguments);
        log.error("Consume [{}] error. Scenario: [{}], ConsumeMode: [{}]. ConsumeStage: [{}]. Message: [{}]. Arguments: [{}]. XId: [{}].",
            param.getFullKey(), scenario, param.getConsumeMode(), consumeStage, argsJson, message, param.getXId(), throwable);
        try {
            doSaveLog(param, consumeStage, scenario, argsJson, message, throwable);
        } catch (Exception e) {
            log.error("Save log error [{}]. Scenario: [{}], ConsumeMode: [{}]. ConsumeStage: [{}]. Message: [{}]. Arguments: [{}]. XId: [{}].",
                param.getFullKey(), scenario, param.getConsumeMode(), consumeStage, argsJson, param.getXId(), e);
        }
    }

    private static final String SAVE_LOG_SQL =
        "insert into t_idempotence_exception_log "
            + "set `namespace` = ?, `prefix` = ?, `key` = ?, `consume_mode` = ?, `x_id` = ?, `consume_stage` = ?, `scenario` = ?, `args` = ?, `exception_msg` = ?, `exception_stack` = ?";

    @SneakyThrows
    private void doSaveLog(IdempotenceParamWrapper param, ConsumeStage consumeStage, IdempotenceScenario scenario, String argsJson, String message, Throwable throwable) {
        Connection connection = null;
        boolean originalAutoCommit = false;
        try {
            connection = dataSource.getConnection();
            originalAutoCommit = ConnectionUtils.setAutoCommitFalse(connection);
            try (PreparedStatement ps = connection.prepareStatement(SAVE_LOG_SQL)) {
                ps.setString(1, param.getNamespace());
                ps.setString(2, param.getPrefix());
                ps.setString(3, param.getKey());
                ps.setString(4, param.getConsumeMode().name());
                ps.setString(5, param.getXId());
                ps.setString(6, consumeStage.name());
                ps.setString(7, scenario.name());
                ps.setObject(8, argsJson);
                if (message.length() > 1024) {
                    message = message.substring(0, 1024);
                }
                ps.setString(9, message);
                StringWriter errors = new StringWriter();
                throwable.printStackTrace(new PrintWriter(errors));
                String stack = errors.toString();
                if (stack.length() > 10240) {
                    stack = stack.substring(0, 10240);
                }
                ps.setString(10, stack);
                ps.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            ConnectionUtils.closeAndResetAutoCommit(connection, originalAutoCommit);
        }
    }

    private static final String SAVE_HIS_SQL =
        "insert into t_idempotence_storage_center_his "
            + "set `namespace` = ?, `prefix` = ?, `key` = ?, `consume_mode` = ?, `x_id` = ?, `consume_status` = ?, `expire_time` = ? ";

    private static final String DELETE_SQL =
        "delete from t_idempotence_storage_center where `id` = ? ";

    @Override
    @SneakyThrows
    public boolean delete(IdempotenceParamWrapper param) {
        Connection connection = null;
        boolean originalAutoCommit = false;
        try {
            connection = dataSource.getConnection();
            originalAutoCommit = ConnectionUtils.setAutoCommitFalse(connection);

            StorageCenterEntity storageCenterEntity = doGet(param, false, connection);
            if (storageCenterEntity == null) {
                return false;
            }

            // 删除记录表里的数据
            int result;
            try (PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
                ps.setLong(1, storageCenterEntity.getId());
                result = ps.executeUpdate();
            }

            // 数据存到his表里面
            if (result == 1) {
                try (PreparedStatement ps = connection.prepareStatement(SAVE_HIS_SQL)) {
                    doSetSaveHistoryPs(storageCenterEntity, ps);
                    ps.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            ConnectionUtils.closeAndResetAutoCommit(connection, originalAutoCommit);
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        delayer.schedule(() -> {
            try {
                clearExpiredKey();
            } catch (Exception e) {
                log.error("清理过期key异常。", e);
            }
        }, 30, TimeUnit.SECONDS);
    }

    @SneakyThrows
    public void clearExpiredKey() {
        Connection connection = null;
        boolean originalAutoCommit = false;
        try {
            connection = dataSource.getConnection();
            originalAutoCommit = ConnectionUtils.setAutoCommitFalse(connection);

            List<StorageCenterEntity> storageCenterEntityList = doGetExpired(connection);
            if (!storageCenterEntityList.isEmpty()) {
                int[] deleteResults;
                try (PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {
                    for (StorageCenterEntity storageCenterEntity : storageCenterEntityList) {
                        ps.setLong(1, storageCenterEntity.getId());
                        ps.addBatch();
                    }
                    deleteResults = ps.executeBatch();
                }

                List<String> deleteKey = new ArrayList<>();
                try (PreparedStatement ps = connection.prepareStatement(SAVE_HIS_SQL)) {
                    for (int i = 0; i < storageCenterEntityList.size(); i++) {
                        int deleteResult = deleteResults[i];
                        if (deleteResult == 1) {
                            StorageCenterEntity storageCenterEntity = storageCenterEntityList.get(i);
                            deleteKey.add(storageCenterEntity.getFullKey());
                            doSetSaveHistoryPs(storageCenterEntity, ps);
                            ps.addBatch();
                        } else if (deleteResult != 0) {
                            log.error("清理过期key-删除操作的结果异常：当前索引[{}]，ID集合[{}]，结果集合[{}]。", i,
                                storageCenterEntityList.stream().map(s -> String.valueOf(s.getId())).collect(Collectors.joining(",")),
                                Arrays.stream(deleteResults).mapToObj(String::valueOf).collect(Collectors.joining(",")));
                        }
                        if (!CollectionUtils.isEmpty(deleteKey)) {
                            ps.executeBatch();
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(deleteKey)) {
                    log.info("清理过期的key: [{}]。", String.join(",", deleteKey));
                }
            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            ConnectionUtils.closeAndResetAutoCommit(connection, originalAutoCommit);
        }
    }

    @SneakyThrows
    private void doSetSaveHistoryPs(StorageCenterEntity storageCenterEntity, PreparedStatement ps) {
        ps.setString(1, storageCenterEntity.getNamespace());
        ps.setString(2, storageCenterEntity.getPrefix());
        ps.setString(3, storageCenterEntity.getKey());
        ps.setString(4, storageCenterEntity.getConsumeMode().name());
        ps.setString(5, storageCenterEntity.getXId());
        ps.setObject(6, storageCenterEntity.getConsumeStatus().name());
        ps.setObject(7, storageCenterEntity.getExpireTime());
    }

    private static final String GET_EXPIRED_SQL =
        "select `id`, `namespace`, `prefix`, `key`, `consume_mode`, `x_id`, `consume_status`, `expire_time` from t_idempotence_storage_center "
            + "where `expire_time` < now() order by `id` asc limit 200";

    @SneakyThrows
    private List<StorageCenterEntity> doGetExpired(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_EXPIRED_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            return assembleEntityList(resultSet);
        }
    }

    @SneakyThrows
    private List<StorageCenterEntity> assembleEntityList(ResultSet resultSet) {
        List<StorageCenterEntity> storageCenterEntityList = new ArrayList<>();
        while (resultSet.next()) {
            StorageCenterEntity storageCenterEntity = new StorageCenterEntity();
            storageCenterEntity.setId(resultSet.getLong("id"));
            storageCenterEntity.setNamespace(resultSet.getString("namespace"));
            storageCenterEntity.setPrefix(resultSet.getString("prefix"));
            storageCenterEntity.setKey(resultSet.getString("key"));
            String consumeMode = resultSet.getString("consume_mode");
            storageCenterEntity.setConsumeMode(consumeMode == null ? null : ConsumeMode.valueOf(consumeMode));
            storageCenterEntity.setXId(resultSet.getString("x_id"));
            String consumeStatus = resultSet.getString("consume_status");
            storageCenterEntity.setConsumeStatus(consumeStatus == null ? null : ConsumeStatus.valueOf(consumeStatus));
            Timestamp expireTime = resultSet.getTimestamp("expire_time");
            storageCenterEntity.setExpireTime(expireTime == null ? null : DateUtils.timestampToLocalDateTime(expireTime.getTime()));
            storageCenterEntityList.add(storageCenterEntity);
        }

        return storageCenterEntityList;
    }

    static final ScheduledThreadPoolExecutor delayer;

    static {
        delayer = new ScheduledThreadPoolExecutor(1, new SimpleNamedThreadFactory("DbStorageCenter-ClearExpiredKey"));
        delayer.setRemoveOnCancelPolicy(true);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            log.error("DbStorageCenter-ClearExpiredKey 开始关闭：{}。", delayer);
            delayer.shutdown();
            if (delayer.awaitTermination(1, TimeUnit.MINUTES)) {
                log.error("DbStorageCenter-ClearExpiredKey 关闭后等待超过1分钟未终止：{}。", delayer);
            }
            log.error("DbStorageCenter-ClearExpiredKey 正常关闭：{}。", delayer);
        } catch (Exception e) {
            log.error("DbStorageCenter-ClearExpiredKey 异常关闭：{}！", delayer, e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
