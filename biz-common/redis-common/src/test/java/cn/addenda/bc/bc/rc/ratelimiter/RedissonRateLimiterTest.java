package cn.addenda.bc.bc.rc.ratelimiter;

import cn.addenda.bc.bc.rc.RedissonClientBaseTest;
import org.junit.Test;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * @author addenda
 * @since 2023/9/11 18:29
 */
public class RedissonRateLimiterTest {

    @Test
    public void test() throws Exception {
        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();

        RRateLimiter rateLimiter = redissonClient.getRateLimiter("redis-common:RedissonRateLimiterTest");
        rateLimiter.trySetRate(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS);
        RRateLimiterWrapper rRateLimiterWrapper = new RRateLimiterWrapper(rateLimiter);
        new RateLimiterBaseTest(rRateLimiterWrapper).test(true);
    }
}
