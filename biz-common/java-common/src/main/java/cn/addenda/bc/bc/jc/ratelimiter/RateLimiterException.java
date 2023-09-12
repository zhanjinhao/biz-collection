package cn.addenda.bc.bc.jc.ratelimiter;

import cn.addenda.bc.bc.jc.JavaCommonException;
import cn.addenda.bc.bc.jc.util.DateUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/9/7 18:19
 */
public class RateLimiterException extends JavaCommonException {

    public RateLimiterException() {
    }

    public RateLimiterException(String message) {
        super(message);
    }

    public RateLimiterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimiterException(Throwable cause) {
        super(cause);
    }

    public RateLimiterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public int getErrorCode() {
        return super.getErrorCode() + 4;
    }

    @Override
    public String getComponentName() {
        return super.getComponentName() + "RateLimiter";
    }

    public static RateLimiterException timeout(long start, int permits, TimeUnit timeUnit, long timeout) {
        LocalDateTime startTime = DateUtils.dateToLocalDateTime(new Date(start));
        LocalDateTime expireTime = startTime.plus(timeout, ChronoUnit.MILLIS);
        String msg = String.format("acquire [%s] permit(s) timeout. start: [%s]. timeout: [%s]ms. expireTime: [%s].",
            permits, DateUtils.format(startTime, DateUtils.FULL_FORMATTER), timeUnit.toMillis(timeout), expireTime);
        return new RateLimiterException(msg);
    }

    public static RateLimiterException exceed(long start, int permits, long maxQueueingTime) {
        LocalDateTime startTime = DateUtils.dateToLocalDateTime(new Date(start));
        String msg = String.format("acquire [%s] permit(s) and exceed max queueing time. now: [%s]. maxQueueingTime: [%s].",
            permits, DateUtils.format(startTime, DateUtils.FULL_FORMATTER), maxQueueingTime);
        return new RateLimiterException(msg);
    }

}
