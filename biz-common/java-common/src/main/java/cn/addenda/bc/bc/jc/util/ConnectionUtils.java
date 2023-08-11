package cn.addenda.bc.bc.jc.util;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 01395265
 * @since 2022/4/28
 */
@Slf4j
public class ConnectionUtils {

    private ConnectionUtils() {
    }

    public static void close(AutoCloseable connection) {
        try {
            connection.close();
        } catch (Exception e) {
            String msg = String.format("关闭 Connection 失败，connection: [%s]。", connection);
            log.error(msg, e);
            throw new UtilException(msg, e);
        }
    }

    public static Connection openConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            String msg = String.format("无法从DataSource中获取connection，dataSource: [%s]。", dataSource);
            log.error(msg, e);
            throw new UtilException(msg, e);
        }
    }

    public static void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            String msg = String.format("rollback 失败，connection: [%s]。", connection);
            log.error(msg, e);
            throw new UtilException(msg, e);
        }
    }

    public static void commit(Connection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            String msg = String.format("commit 失败，connection: [%s]。", connection);
            log.error(msg, e);
            throw new UtilException(msg, e);
        }
    }

    /**
     * @return 此方法执行执行之前连接是否是自动提交
     */
    public static int setAutoCommit(Connection connection, boolean expect) {
        try {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(expect);
            return autoCommit ? 1 : 0;
        } catch (SQLException e) {
            String msg = String.format("设置 connection [%s] 的 autoCommit 为 [%s] 失败。", connection, expect);
            log.error(msg, e);
            throw new UtilException(msg, e);
        }
    }

    /**
     * @return 此方法执行之前，事务的隔离级别
     */
    public static int setTransactionIsolation(Connection connection, int expect) {
        try {
            int transactionIsolation = connection.getTransactionIsolation();
            connection.setTransactionIsolation(expect);
            return transactionIsolation;
        } catch (SQLException e) {
            String msg = String.format("设置 connection [%s] 的 事务隔离级别为 [%s] 失败。", connection, expect);
            log.error(msg, e);
            throw new UtilException(msg, e);
        }
    }

}
