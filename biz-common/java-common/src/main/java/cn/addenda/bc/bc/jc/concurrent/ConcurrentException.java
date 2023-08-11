package cn.addenda.bc.bc.jc.concurrent;

import cn.addenda.bc.bc.jc.JavaCommonException;

/**
 * @author addenda
 * @since 2022/12/8 23:21
 */
public class ConcurrentException extends JavaCommonException {

    public ConcurrentException() {
    }

    public ConcurrentException(String message) {
        super(message);
    }

    public ConcurrentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentException(Throwable cause) {
        super(cause);
    }

    public ConcurrentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 3;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "Concurrent";
    }

}
