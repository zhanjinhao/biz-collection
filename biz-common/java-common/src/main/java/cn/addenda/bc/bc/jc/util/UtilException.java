package cn.addenda.bc.bc.jc.util;

import cn.addenda.bc.bc.jc.JavaCommonException;

/**
 * 这个异常正常情况下是不应该出现的，因为工具类的实现定义了确定的输入输出。
 * 在业务系统，由于依赖用户的输入，确定的输入往往很难达到。
 *
 * @author addenda
 * @since 2022/2/14 19:16
 */
public class UtilException extends JavaCommonException {
    public UtilException() {
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public UtilException(Throwable cause) {
        super(cause);
    }

    public UtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 1;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "Util";
    }
}
