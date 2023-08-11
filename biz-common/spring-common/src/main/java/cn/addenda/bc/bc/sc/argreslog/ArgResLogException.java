package cn.addenda.bc.bc.sc.argreslog;

import cn.addenda.bc.bc.sc.SpringCommonException;

/**
 * @author addenda
 * @since 2023/3/9 11:21
 */
public class ArgResLogException extends SpringCommonException {

    public ArgResLogException() {
    }

    public ArgResLogException(String message) {
        super(message);
    }

    public ArgResLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgResLogException(Throwable cause) {
        super(cause);
    }

    public ArgResLogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 5;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "ArgResLog";
    }
}
