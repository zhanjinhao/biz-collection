package cn.addenda.bc.bc.rc.lockallocator;

import cn.addenda.bc.bc.jc.concurrent.allocator.LockAllocator;
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
    public RLock allocateLock(String name) {
        return redissonClient.getLock(name);
    }

    @Override
    public void releaseLock(String name) {

    }
}
