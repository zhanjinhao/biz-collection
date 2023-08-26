package cn.addenda.bc.bc.tc;

import cn.addenda.bc.bc.SystemException;

/**
 * @author addenda
 * @since 2023/8/19 12:58
 */
public class TomcatCommonException extends SystemException {

    public TomcatCommonException() {
    }

    public TomcatCommonException(String message) {
        super(message);
    }

    public TomcatCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public TomcatCommonException(Throwable cause) {
        super(cause);
    }

    public TomcatCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return 103_000;
    }

    @Override
    public String getComponentName() {
        return "tomcat-common: ";
    }

}
