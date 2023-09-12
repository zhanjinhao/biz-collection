package cn.addenda.bc.bc.rc.ratelimiter;

import cn.addenda.bc.bc.jc.ratelimiter.MultiPermitsRateLimiter;
import cn.addenda.bc.bc.jc.ratelimiter.RateLimiterException;
import org.redisson.api.RRateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/9/8 20:38
 */
public class RedissonRateLimiterWrapper implements MultiPermitsRateLimiter {

    private final RRateLimiter rateLimiter;

    public RedissonRateLimiterWrapper(RRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void acquire() {
        rateLimiter.acquire();
    }

    @Override
    public void acquire(TimeUnit timeUnit, long timeout) {
        long now = System.currentTimeMillis();
        if (!tryAcquire(timeUnit, timeout)) {
            throw RateLimiterException.timeout(now, 1, timeUnit, timeout);
        }
    }

    @Override
    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }

    @Override
    public boolean tryAcquire(TimeUnit timeUnit, long timeout) {
        return rateLimiter.tryAcquire(timeout, timeUnit);
    }

    @Override
    public void acquire(int permits) {
        rateLimiter.acquire(permits);
    }

    @Override
    public void acquire(int permits, TimeUnit timeUnit, long timeout) {
        long now = System.currentTimeMillis();
        if (!tryAcquire(permits, timeUnit, timeout)) {
            throw RateLimiterException.timeout(now, permits, timeUnit, timeout);
        }
    }

    @Override
    public boolean tryAcquire(int permits) {
        return rateLimiter.tryAcquire(permits);
    }

    @Override
    public boolean tryAcquire(int permits, TimeUnit timeUnit, long timeout) {
        return rateLimiter.tryAcquire(permits, timeout, timeUnit);
    }

}
