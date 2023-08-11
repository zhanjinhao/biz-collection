package cn.addenda.bc.bc.sc;

import cn.addenda.bc.bc.SystemException;

/**
 * @author addenda
 * @since 2022/8/7 12:06
 */
public class SpringCommonException extends SystemException {

    public SpringCommonException() {
    }

    public SpringCommonException(String message) {
        super(message);
    }

    public SpringCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpringCommonException(Throwable cause) {
        super(cause);
    }

    public SpringCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return 102_000;
    }

    @Override
    public String getComponentName() {
        return "spring-common: ";
    }

}
