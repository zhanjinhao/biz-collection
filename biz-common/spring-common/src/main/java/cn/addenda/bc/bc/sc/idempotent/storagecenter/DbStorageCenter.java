package cn.addenda.bc.bc.sc.idempotent.storagecenter;

import cn.addenda.bc.bc.jc.util.JacksonUtils;
import cn.addenda.bc.bc.sc.idempotent.ConsumeStatus;
import cn.addenda.bc.bc.sc.idempotent.IdempotentException;
import cn.addenda.bc.bc.sc.idempotent.IdempotentScenario;
import cn.addenda.bc.bc.sc.lock.LockHelper;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2023/8/3 14:53
 */
@Slf4j
public class DbStorageCenter implements StorageCenter {

    private final DataSource dataSource;

    private final LockHelper lockHelper;

    public DbStorageCenter(DataSource dataSource, LockHelper lockHelper) {
        this.dataSource = dataSource;
        this.lockHelper = lockHelper;
    }

    private static final String GET_SQL =
        "select `consume_status` from t_idempotent_storage_center "
            + "where `namespace` = ? and `prefix` = ? and `key` = ? and `consume_mode` = ? and `expire_time` > now() and `if_del` = 0";

    @Override
    public ConsumeStatus get(IdempotentParamWrapper paramWrapper) {
        return doGet(paramWrapper, false);
    }

    private ConsumeStatus doGet(IdempotentParamWrapper paramWrapper, boolean nullAble) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(GET_SQL)) {
                preparedStatement.setString(1, paramWrapper.getNamespace());
                preparedStatement.setString(2, paramWrapper.getPrefix());
                preparedStatement.setString(3, paramWrapper.getKey());
                preparedStatement.setString(4, paramWrapper.getConsumeMode().name());
                ResultSet resultSet = preparedStatement.executeQuery();
                List<String> consumeStatusList = new ArrayList<>();
                while (resultSet.next()) {
                    consumeStatusList.add(resultSet.getString("consume_status"));
                }
                if (consumeStatusList.isEmpty()) {
                    if (nullAble) {
                        return null;
                    } else {
                        String msg = String.format("Get ConsumeStatus from [%s] error. Result is Empty.", paramWrapper);
                        throw new IdempotentException(msg);
                    }
                } else if (consumeStatusList.size() > 1) {
                    String msg = String.format("Get ConsumeStatus from [%s] error. Result has multi records.", paramWrapper);
                    throw new IdempotentException(msg);
                } else {
                    return ConsumeStatus.valueOf(consumeStatusList.get(0));
                }
            }
        } catch (SQLException e) {
            throw new IdempotentException(e);
        }
    }

    private static final String SAVE_SQL =
        "insert into t_idempotent_storage_center "
            + "set `namespace` = ?, `prefix` = ?, `key` = ?, `consume_mode` = ?, `expire_time` = ?, `consume_status` = ?, `if_del` = ?";

    @Override
    public boolean saveIfAbsent(IdempotentParamWrapper paramWrapper, ConsumeStatus consumeStatus) {
        return lockHelper.doLock(() -> {
            ConsumeStatus oldConsumeStatus = doGet(paramWrapper, true);
            if (oldConsumeStatus == null) {
                try (Connection connection = dataSource.getConnection()) {
                    boolean autoCommit = connection.getAutoCommit();
                    connection.setAutoCommit(false);
                    try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL)) {
                        preparedStatement.setString(1, paramWrapper.getNamespace());
                        preparedStatement.setString(2, paramWrapper.getPrefix());
                        preparedStatement.setString(3, paramWrapper.getKey());
                        preparedStatement.setString(4, paramWrapper.getConsumeMode().name());
                        preparedStatement.setObject(5, LocalDateTime.now().plus(paramWrapper.getTimeoutSecs(), ChronoUnit.SECONDS));
                        preparedStatement.setObject(6, consumeStatus.name());
                        preparedStatement.executeUpdate();
                    }
                    connection.commit();
                    connection.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    throw new IdempotentException(e);
                }
                return true;
            }
            return false;
        }, paramWrapper.getFullKey());
    }

    private static final String UPDATE_SQL =
        "update t_idempotent_storage_center "
            + "set `consume_status` = ?, `expire_time` = ? where `namespace` = ? and `prefix` = ? and `key` = ? and `consume_mode` = ? and `if_del` = 0";

    @Override
    public void modifyStatus(IdempotentParamWrapper paramWrapper, ConsumeStatus consumeStatus) {
        try (Connection connection = dataSource.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
                preparedStatement.setString(1, consumeStatus.name());
                preparedStatement.setObject(2, LocalDateTime.now().plus(paramWrapper.getTimeoutSecs(), ChronoUnit.SECONDS));
                preparedStatement.setString(3, paramWrapper.getNamespace());
                preparedStatement.setString(4, paramWrapper.getPrefix());
                preparedStatement.setString(5, paramWrapper.getKey());
                preparedStatement.setString(6, paramWrapper.getConsumeMode().name());
                preparedStatement.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new IdempotentException(e);
        }
    }

    private static final String DELETE_SQL =
        "update t_idempotent_storage_center "
            + "set if_del = 1 where `namespace` = ? and `prefix` = ? and `key` = ? and `consume_mode` = ? and `if_del` = 0";

    @Override
    public void delete(IdempotentParamWrapper paramWrapper) {
        try (Connection connection = dataSource.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
                preparedStatement.setString(1, paramWrapper.getNamespace());
                preparedStatement.setString(2, paramWrapper.getPrefix());
                preparedStatement.setString(3, paramWrapper.getKey());
                preparedStatement.setString(4, paramWrapper.getConsumeMode().name());
                preparedStatement.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new IdempotentException(e);
        }
    }

    private static final String SAVE_LOG_SQL =
        "insert into t_idempotent_exception_log "
            + "set `namespace` = ?, `prefix` = ?, `key` = ?, `consume_mode` = ?, `args` = ?, `exception_msg` = ?, `exception_stack` = ? ";

    @Override
    public Object exceptionCallback(IdempotentParamWrapper param, IdempotentScenario scenario, Object[] arguments, Throwable throwable) throws Throwable {
        try {
            switch (scenario) {
                case MQ:
                    String argsJson = JacksonUtils.objectToString(arguments);
                    log.error("[{}] Consume error. Mode: [{}]. Arguments: [{}].",
                        param, param.getConsumeMode(), argsJson, throwable);
                    try (Connection connection = dataSource.getConnection()) {
                        boolean autoCommit = connection.getAutoCommit();
                        connection.setAutoCommit(false);
                        try (PreparedStatement preparedStatement = connection.prepareStatement(SAVE_LOG_SQL)) {
                            preparedStatement.setString(1, param.getNamespace());
                            preparedStatement.setString(2, param.getPrefix());
                            preparedStatement.setString(3, param.getKey());
                            preparedStatement.setString(4, param.getConsumeMode().name());
                            preparedStatement.setObject(5, argsJson);
                            String message = throwable.getMessage();
                            if (message.length() > 1000) {
                                message = message.substring(0, 1000);
                            }
                            preparedStatement.setString(6, message);
                            StringWriter errors = new StringWriter();
                            throwable.printStackTrace(new PrintWriter(errors));
                            String stack = errors.toString();
                            if (stack.length() > 1000) {
                                stack = stack.substring(0, 1000);
                            }
                            preparedStatement.setString(7, stack);
                            preparedStatement.executeUpdate();
                        }
                        connection.commit();
                        connection.setAutoCommit(autoCommit);
                    } catch (SQLException e) {
                        throw new IdempotentException(e);
                    }
                    return null;
                case REST:
                    throw throwable;
                default: // unreachable
                    return null;
            }
        } finally {
            modifyStatus(param, ConsumeStatus.EXCEPTION);
        }
    }

}
