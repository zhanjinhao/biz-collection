package cn.addenda.bc.bc.rc.cache;

import cn.addenda.bc.bc.jc.cache.CacheHelper;
import cn.addenda.bc.bc.jc.concurrent.allocator.LockAllocator;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * @author addenda
 * @since 2023/6/3 16:57
 */
public class RedisCacheHelper extends CacheHelper {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisCacheHelper(StringRedisTemplate stringRedisTemplate, long ppfExpirationDetectionInterval,
                            LockAllocator<? extends Lock> lockAllocator, ExecutorService cacheBuildEs) {
        super(new RedisKVCache(stringRedisTemplate), ppfExpirationDetectionInterval, lockAllocator, cacheBuildEs);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public RedisCacheHelper(StringRedisTemplate stringRedisTemplate, long ppfExpirationDetectionInterval,
                            LockAllocator<? extends Lock> lockAllocator) {
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
