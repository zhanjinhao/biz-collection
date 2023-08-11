package cn.addenda.bc.bc.mc.resulthandler;

import cn.addenda.bc.bc.mc.MybatisCommonException;

/**
 * @author addenda
 * @since 2022/2/1 17:33
 */
public class ResultHelperException extends MybatisCommonException {
    public ResultHelperException() {
    }

    public ResultHelperException(String message) {
        super(message);
    }

    public ResultHelperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResultHelperException(Throwable cause) {
        super(cause);
    }

    public ResultHelperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 1;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "ResultHelper";
    }

}
