package cn.addenda.bc.bc.rc.allocator;

import cn.addenda.bc.bc.jc.allocator.AllocatorException;
import cn.addenda.bc.bc.jc.allocator.AutoExpiredAllocator;
import cn.addenda.bc.bc.jc.allocator.DefaultAutoExpiredAllocator;
import cn.addenda.bc.bc.rc.ratelimiter.ExpiredRedissonRateLimiter;
import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author addenda
 * @since 2023/9/16 11:53
 */
public class RedissonRateLimiterAutoExpiredAllocator implements AutoExpiredAllocator<RRateLimiter> {

    private final RateType mode;
    private final long rate;
    private final long rateInterval;
    private final RateIntervalUnit rateIntervalUnit;
    private final RedissonClient redissonClient;
    private final CommandAsyncExecutor executor;

    private final AutoExpiredAllocator<ExpiredRedissonRateLimiter> allocator;

    public RedissonRateLimiterAutoExpiredAllocator(
        RateType mode, long rate, long rateInterval,
        RateIntervalUnit rateIntervalUnit, RedissonClient redissonClient) {
        this.mode = mode;
        this.rate = rate;
        this.rateInterval = rateInterval;
        this.rateIntervalUnit = rateIntervalUnit;
        if (!(redissonClient instanceof Redisson)) {
            throw new AllocatorException("RedissonClient 只支持 org.redisson.Redisson。");
        }
        this.redissonClient = redissonClient;
        this.executor = extractCommandAsyncExecutor();
        this.allocator = new DefaultAutoExpiredAllocator<ExpiredRedissonRateLimiter>() {
            @Override
            protected Function<Param, ExpiredRedissonRateLimiter> referenceFunction() {
                return new Function<Param, ExpiredRedissonRateLimiter>() {
                    @Override
                    public ExpiredRedissonRateLimiter apply(Param param) {
                        String name = param.getName();
                        TimeUnit timeUnit = param.getTimeUnit();
                        long timeout = param.getTimeout();
                        return new ExpiredRedissonRateLimiter(
                            executor, name, mode, rate, rateInterval, rateIntervalUnit, timeUnit, timeout);
                    }
                };
            }
        };
    }

    @Override
    public RRateLimiter allocate(String name) {
        return allocate(name, TimeUnit.DAYS, 36500);
    }

    @Override
    public void release(String name) {
        allocator.allocate(name).delete();
    }

    @Override
    public RRateLimiter allocate(String name, TimeUnit timeUnit, long timeout) {
        return allocator.allocate(name, timeUnit, timeout);
    }

    private CommandAsyncExecutor extractCommandAsyncExecutor() {
        Field commandExecutorField = ReflectionUtils.findField(Redisson.class, "commandExecutor");
        ReflectionUtils.makeAccessible(commandExecutorField);
        return (CommandAsyncExecutor) ReflectionUtils.getField(commandExecutorField, redissonClient);
    }

    @Override
    public String toString() {
        return "RedissonRateLimiterAutoExpiredAllocator{" +
            "mode=" + mode +
            ", rate=" + rate +
            ", rateInterval=" + rateInterval +
            ", rateIntervalUnit=" + rateIntervalUnit +
            '}';
    }
}
