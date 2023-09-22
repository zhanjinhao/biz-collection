package cn.addenda.bc.bc.rc.allocator;

import cn.addenda.bc.bc.rc.RedissonClientBaseTest;
import cn.addenda.bc.bc.rc.ratelimiter.RRateLimiterWrapper;
import cn.addenda.bc.bc.rc.ratelimiter.RateLimiterBaseTest;
import org.junit.Test;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * @author addenda
 * @since 2023/9/21 22:26
 */
public class RedissonRateLimiterAutoExpiredAllocatorTest {

    @Test
    public void test1() throws Exception {
        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();
        RedissonRateLimiterAutoExpiredAllocator allocator = new RedissonRateLimiterAutoExpiredAllocator(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS, redissonClient);

        RRateLimiter rateLimiter = allocator.allocate("redis-common:RedissonRateLimiterAutoExpiredAllocatorTest");
        new RateLimiterBaseTest(new RRateLimiterWrapper(rateLimiter)).test(true);
    }

    @Test
    public void test2() throws Exception {
        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();
        RedissonRateLimiterAutoExpiredAllocator allocator = new RedissonRateLimiterAutoExpiredAllocator(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS, redissonClient);

        RRateLimiter rateLimiter = allocator.allocate("redis-common:RedissonRateLimiterAutoExpiredAllocatorTest");
        System.out.println(rateLimiter.availablePermits());
    }

    @Test
    public void test3() throws Exception {
        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();
        RedissonRateLimiterAutoExpiredAllocator allocator = new RedissonRateLimiterAutoExpiredAllocator(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS, redissonClient);

        allocator.release("redis-common:RedissonRateLimiterAutoExpiredAllocatorTest");
    }

}
