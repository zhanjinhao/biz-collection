package cn.addenda.bc.bc.mc.typehandler;

import cn.addenda.bc.bc.mc.MybatisCommonException;

/**
 * @author addenda
 * @since 2022/8/7 12:30
 */
public class TypeHandlerException extends MybatisCommonException {
    public TypeHandlerException() {
    }

    public TypeHandlerException(String message) {
        super(message);
    }

    public TypeHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeHandlerException(Throwable cause) {
        super(cause);
    }

    public TypeHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 3;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "TypeHandler";
    }
}
