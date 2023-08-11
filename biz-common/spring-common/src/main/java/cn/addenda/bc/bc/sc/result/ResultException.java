package cn.addenda.bc.bc.sc.result;

import cn.addenda.bc.bc.sc.SpringCommonException;

/**
 * @author addenda
 * @since  2022/4/8
 */
public class ResultException extends SpringCommonException {
    public ResultException() {
    }

    public ResultException(String message) {
        super(message);
    }

    public ResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResultException(Throwable cause) {
        super(cause);
    }

    public ResultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 6;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "Result";
    }

}
