package cn.addenda.bc.bc.jc.cache;

import cn.addenda.bc.bc.jc.JavaCommonException;

/**
 * @author addenda
 * @since 2023/05/30 19:16
 */
public class CacheException extends JavaCommonException {

    public CacheException() {
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 2;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "Cache";
    }

}
