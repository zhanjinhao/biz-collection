package cn.addenda.bc.bc.jc.allocator;

import cn.addenda.bc.bc.jc.JavaCommonException;

/**
 * @author addenda
 * @since 2023/9/16 12:24
 */
public class AllocatorException extends JavaCommonException {

    public AllocatorException() {
    }

    public AllocatorException(String message) {
        super(message);
    }

    public AllocatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AllocatorException(Throwable cause) {
        super(cause);
    }

    public AllocatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 5;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "Allocator";
    }

}
