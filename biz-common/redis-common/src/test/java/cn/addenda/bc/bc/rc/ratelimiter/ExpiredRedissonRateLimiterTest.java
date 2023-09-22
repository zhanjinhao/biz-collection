package cn.addenda.bc.bc.rc.ratelimiter;

import cn.addenda.bc.bc.rc.RedissonClientBaseTest;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/9/11 18:29
 */
public class ExpiredRedissonRateLimiterTest {

    @Test
    public void test() throws Exception {

        RedissonClient redissonClient = RedissonClientBaseTest.redissonClient();

        ExpiredRedissonRateLimiter rateLimiter = new ExpiredRedissonRateLimiter(
            extractCommandAsyncExecutor(redissonClient), "redis-common:ExpiredRedissonRateLimiterTest",
            RateType.OVERALL, 10, 10, RateIntervalUnit.SECONDS, TimeUnit.SECONDS, 1000);

        RRateLimiterWrapper rRateLimiterWrapper = new RRateLimiterWrapper(rateLimiter);
        new RateLimiterBaseTest(rRateLimiterWrapper).test(true);
    }

    private CommandAsyncExecutor extractCommandAsyncExecutor(RedissonClient redissonClient) {
        Field commandExecutorField = ReflectionUtils.findField(Redisson.class, "commandExecutor");
        ReflectionUtils.makeAccessible(commandExecutorField);
        return (CommandAsyncExecutor) ReflectionUtils.getField(commandExecutorField, redissonClient);
    }
}
