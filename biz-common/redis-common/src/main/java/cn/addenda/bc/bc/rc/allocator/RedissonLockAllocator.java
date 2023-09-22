package cn.addenda.bc.bc.rc.allocator;

import cn.addenda.bc.bc.jc.allocator.lock.LockAllocator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * @author addenda
 * @since 2023/6/3 16:01
 */
public class RedissonLockAllocator implements LockAllocator<RLock> {

    private final RedissonClient redissonClient;

    public RedissonLockAllocator(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public RLock allocate(String name) {
        return redissonClient.getLock(name);
    }

    @Override
    public void release(String name) {

    }
}
