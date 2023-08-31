package cn.addenda.bc.bc.jc.lockallocation;

import cn.addenda.bc.bc.jc.concurrent.ConcurrentException;
import cn.addenda.bc.bc.jc.concurrent.allocator.LockAllocator;
import cn.addenda.bc.bc.jc.pojo.Binary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author addenda
 * @since 2023/5/30 22:51
 */
public class Impl_CAS_Segment implements LockAllocator<Lock> {

    private final Map<String, Binary<Lock, Integer>> lockMap = new ConcurrentHashMap<>();

    private final AtomicInteger[] locks;

    public Impl_CAS_Segment() {
        locks = new AtomicInteger[2 << 4];
        for (int i = 0; i < 2 << 4; i++) {
            locks[i] = new AtomicInteger(0);
        }
    }

    @Override
    public Lock allocateLock(String name) {
        AtomicInteger atomicInteger = locks[index(name)];
        while (!atomicInteger.compareAndSet(0, 1)) {
        }
        try {
            Binary<Lock, Integer> lockBinary = lockMap
                .computeIfAbsent(name, s -> new Binary<>(new ReentrantLock(), 0));
            lockBinary.setF2(lockBinary.getF2() + 1);
            return lockBinary.getF1();
        } finally {
            while (!atomicInteger.compareAndSet(1, 0)) {
            }
        }

    }

    @Override
    public void releaseLock(String name) {
        AtomicInteger atomicInteger = locks[index(name)];
        while (!atomicInteger.compareAndSet(0, 1)) {
        }
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
            while (!atomicInteger.compareAndSet(1, 0)) {
            }
        }
    }

    public Map<String, Binary<Lock, Integer>> getLockMap() {
        return lockMap;
    }

    private int index(String name) {
        return name.hashCode() & ((2 << 4) - 1);
    }

}
