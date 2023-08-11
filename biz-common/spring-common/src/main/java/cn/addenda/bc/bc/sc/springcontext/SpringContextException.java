package cn.addenda.bc.bc.sc.springcontext;

import cn.addenda.bc.bc.sc.SpringCommonException;

/**
 * @author addenda
 * @since 2022/8/7 12:06
 */
public class SpringContextException extends SpringCommonException {
    public SpringContextException() {
    }

    public SpringContextException(String message) {
        super(message);
    }

    public SpringContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpringContextException(Throwable cause) {
        super(cause);
    }

    public SpringContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 3;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "SpringContext";
    }
}
