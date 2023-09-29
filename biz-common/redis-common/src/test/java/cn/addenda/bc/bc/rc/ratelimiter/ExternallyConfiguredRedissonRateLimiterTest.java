package cn.addenda.bc.bc.rc.ratelimiter;

import cn.addenda.bc.bc.rc.RedissonClientBaseTest;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateLimiterConfig;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author addenda
 * @since 2023/9/11 18:29
 */
public class ExternallyConfiguredRedissonRateLimiterTest {

    @Test
    public void test() throws Exception {

        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();

        ExternallyConfiguredRedissonRateLimiter rateLimiter = new ExternallyConfiguredRedissonRateLimiter(
            extractCommandAsyncExecutor(redissonClient), "yanzhengma",
            "redis-common:ExternallyConfiguredRedissonRateLimiterTest");

        rateLimiter.trySetRate(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS);

        RateLimiterConfig config = rateLimiter.getConfig();
        System.out.println(config.getRateType());
        System.out.println(config.getRate());
        // 单位是毫秒
        System.out.println(config.getRateInterval());

        System.out.println(rateLimiter.availablePermits());

        RRateLimiterWrapper rRateLimiterWrapper = new RRateLimiterWrapper(rateLimiter);
        new RateLimiterBaseTest(rRateLimiterWrapper).test(true);
    }

    @Test
    public void test2() throws Exception {

        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();

        ExternallyConfiguredRedissonRateLimiter rateLimiter = new ExternallyConfiguredRedissonRateLimiter(
            extractCommandAsyncExecutor(redissonClient), "yanzhengma",
            "redis-common:ExternallyConfiguredRedissonRateLimiterTest", 50000);

        rateLimiter.trySetRate(RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS);

        RateLimiterConfig config = rateLimiter.getConfig();
        System.out.println(config.getRateType());
        System.out.println(config.getRate());
        // 单位是毫秒
        System.out.println(config.getRateInterval());

        System.out.println(rateLimiter.availablePermits());

        RRateLimiterWrapper rRateLimiterWrapper = new RRateLimiterWrapper(rateLimiter);
        new RateLimiterBaseTest(rRateLimiterWrapper).test(true);
    }

    @Test
    public void test3() throws Exception {
        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();

        ExternallyConfiguredRedissonRateLimiter rateLimiter = new ExternallyConfiguredRedissonRateLimiter(
            extractCommandAsyncExecutor(redissonClient), "yanzhengma",
            "redis-common:ExternallyConfiguredRedissonRateLimiterTest", 50000);

        rateLimiter.setRate(RateType.OVERALL, 10, 200, RateIntervalUnit.SECONDS);

        rateLimiter.delete();
    }

    private CommandAsyncExecutor extractCommandAsyncExecutor(RedissonClient redissonClient) {
        Field commandExecutorField = ReflectionUtils.findField(Redisson.class, "commandExecutor");
        ReflectionUtils.makeAccessible(commandExecutorField);
        return (CommandAsyncExecutor) ReflectionUtils.getField(commandExecutorField, redissonClient);
    }
}
