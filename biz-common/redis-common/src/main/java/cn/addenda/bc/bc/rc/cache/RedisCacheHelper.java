package cn.addenda.bc.bc.rc.cache;

import cn.addenda.bc.bc.jc.allocator.lock.LockAllocator;
import cn.addenda.bc.bc.jc.allocator.trafficlimit.TrafficLimiterAllocator;
import cn.addenda.bc.bc.jc.cache.CacheHelper;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * @author addenda
 * @since 2023/6/3 16:57
 */
public class RedisCacheHelper extends CacheHelper {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisCacheHelper(StringRedisTemplate stringRedisTemplate, long ppfExpirationDetectionInterval, LockAllocator<?> lockAllocator,
                            ExecutorService cacheBuildEs, TrafficLimiterAllocator<?> realQueryTrafficLimiterAllocator, boolean useServiceException) {
        super(new RedisKVCache(stringRedisTemplate), ppfExpirationDetectionInterval, lockAllocator, cacheBuildEs, realQueryTrafficLimiterAllocator, useServiceException);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public RedisCacheHelper(StringRedisTemplate stringRedisTemplate, long ppfExpirationDetectionInterval, LockAllocator<? extends Lock> lockAllocator) {
        super(new RedisKVCache(stringRedisTemplate), ppfExpirationDetectionInterval, lockAllocator);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public RedisCacheHelper(StringRedisTemplate stringRedisTemplate, long ppfExpirationDetectionInterval) {
        super(new RedisKVCache(stringRedisTemplate), ppfExpirationDetectionInterval);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public RedisCacheHelper(StringRedisTemplate stringRedisTemplate) {
        super(new RedisKVCache(stringRedisTemplate));
        this.stringRedisTemplate = stringRedisTemplate;
    }

}
