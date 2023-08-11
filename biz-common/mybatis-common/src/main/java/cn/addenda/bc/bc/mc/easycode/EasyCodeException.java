package cn.addenda.bc.bc.mc.easycode;

import cn.addenda.bc.bc.mc.MybatisCommonException;

/**
 * @author addenda
 * @since 2023/7/6 10:13
 */
public class EasyCodeException extends MybatisCommonException {

    public EasyCodeException() {
    }

    public EasyCodeException(String message) {
        super(message);
    }

    public EasyCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyCodeException(Throwable cause) {
        super(cause);
    }

    public EasyCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 4;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "EasyCode";
    }
}
