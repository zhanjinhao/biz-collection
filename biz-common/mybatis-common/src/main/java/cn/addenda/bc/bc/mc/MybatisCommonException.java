package cn.addenda.bc.bc.mc;

import cn.addenda.bc.bc.SystemException;

/**
 * @author addenda
 * @since 2023/5/30 22:21
 */
public class MybatisCommonException extends SystemException {

    public MybatisCommonException() {
    }

    public MybatisCommonException(String message) {
        super(message);
    }

    public MybatisCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisCommonException(Throwable cause) {
        super(cause);
    }

    public MybatisCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return 101_000;
    }

    @Override
    public String getComponentName() {
        return "mybatis-common: ";
    }

}
