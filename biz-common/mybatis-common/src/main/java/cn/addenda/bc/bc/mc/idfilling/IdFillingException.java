package cn.addenda.bc.bc.mc.idfilling;

import cn.addenda.bc.bc.mc.MybatisCommonException;

/**
 * @author addenda
 * @since 2022/2/5 22:45
 */
public class IdFillingException extends MybatisCommonException {

    public IdFillingException() {
    }

    public IdFillingException(String message) {
        super(message);
    }

    public IdFillingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdFillingException(Throwable cause) {
        super(cause);
    }

    public IdFillingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 2;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "IdFilling";
    }
}
