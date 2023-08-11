package cn.addenda.bc.bc.rc.cache;

import cn.addenda.bc.bc.jc.cache.KVCache;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/6/3 16:25
 */
public class RedisKVCache implements KVCache<String, String> {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisKVCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public boolean containsKey(String s) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(s));
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long capacity() {
        return Long.MAX_VALUE;
    }

}
