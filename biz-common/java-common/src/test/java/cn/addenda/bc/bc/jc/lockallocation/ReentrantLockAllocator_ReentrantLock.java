package cn.addenda.bc.bc.jc.lockallocation;

import cn.addenda.bc.bc.jc.concurrent.ConcurrentException;
import cn.addenda.bc.bc.jc.concurrent.allocator.LockAllocator;
import cn.addenda.bc.bc.jc.pojo.Binary;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author addenda
 * @since 2023/5/30 22:51
 */
public class ReentrantLockAllocator_ReentrantLock implements LockAllocator<Lock> {

    private final Map<String, Binary<Lock, Integer>> lockMap = new HashMap<>();

    private final Lock lock = new ReentrantLock();

    @Override
    public Lock allocateLock(String name) {
        lock.lock();
        try {
            Binary<Lock, Integer> lockBinary = lockMap
                    .computeIfAbsent(name, s -> new Binary<>(new ReentrantLock(), 0));
            lockBinary.setF2(lockBinary.getF2() + 1);
            return lockBinary.getF1();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseLock(String name) {
        lock.lock();
        try {
            Binary<Lock, Integer> lockBinary = lockMap.get(name);
            if (lockBinary == null) {
                String msg = String.format("锁 [%s] 不存在！", name);
                throw new ConcurrentException(msg);
            }

            lockBinary.setF2(lockBinary.getF2() - 1);
            int i = lockBinary.getF2();
            if (i == 0) {
                lockMap.remove(name);
            }
        } finally {
            lock.unlock();
        }
    }

    public Map<String, Binary<Lock, Integer>> getLockMap() {
        return lockMap;
    }
}
