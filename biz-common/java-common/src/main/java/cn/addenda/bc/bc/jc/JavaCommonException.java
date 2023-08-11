package cn.addenda.bc.bc.jc;

import cn.addenda.bc.bc.SystemException;

/**
 * @author addenda
 * @since 2023/5/30 22:18
 */
public class JavaCommonException extends SystemException {

    public JavaCommonException() {
    }

    public JavaCommonException(String message) {
        super(message);
    }

    public JavaCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavaCommonException(Throwable cause) {
        super(cause);
    }

    public JavaCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return 100_000;
    }

    @Override
    public String getComponentName() {
        return "java-common: ";
    }

}
