package cn.addenda.bc.bc.rc.allocator;

import cn.addenda.bc.bc.jc.ratelimiter.RateLimiter;
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
public class ExpiredRedissonRateLimiterAllocatorTest {

    @Test
    public void test1() throws Exception {
        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();
        ExpiredRedissonRateLimiterAllocator allocator = new ExpiredRedissonRateLimiterAllocator(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS, redissonClient);

        RateLimiter rateLimiter = allocator.allocate("redis-common:ExpiredRedissonRateLimiterAllocatorTest");
        new RateLimiterBaseTest(rateLimiter).test(true);
    }

    @Test
    public void test2() throws Exception {
        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();
        ExpiredRedissonRateLimiterAllocator allocator = new ExpiredRedissonRateLimiterAllocator(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS, redissonClient);

        RRateLimiterWrapper rateLimiter = allocator.allocate("redis-common:ExpiredRedissonRateLimiterAllocatorTest");
        RRateLimiter rRateLimiter = rateLimiter.getRateLimiter();
        System.out.println(rRateLimiter.availablePermits());
    }

    @Test
    public void test3() throws Exception {
        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();
        ExpiredRedissonRateLimiterAllocator allocator = new ExpiredRedissonRateLimiterAllocator(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS, redissonClient);

        allocator.release("redis-common:ExpiredRedissonRateLimiterAllocatorTest");
    }

}
