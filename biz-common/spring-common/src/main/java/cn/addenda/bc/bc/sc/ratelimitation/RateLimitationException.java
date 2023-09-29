package cn.addenda.bc.bc.sc.ratelimitation;

import cn.addenda.bc.bc.sc.SpringCommonException;

/**
 * @author addenda
 * @since 2023/9/28 20:04
 */
public class RateLimitationException extends SpringCommonException {
    public RateLimitationException() {
    }

    public RateLimitationException(String message) {
        super(message);
    }

    public RateLimitationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimitationException(Throwable cause) {
        super(cause);
    }

    public RateLimitationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 7;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "RateLimitation";
    }
}
