package cn.addenda.bc.bc.jc.lockallocation;

import cn.addenda.bc.bc.jc.allocator.lock.LockAllocator;
import cn.addenda.bc.bc.jc.concurrent.ConcurrentException;
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
public class Impl_Synchronized_Segment implements LockAllocator<Lock> {

    private final Map<String, Binary<Lock, AtomicInteger>> lockMap = new ConcurrentHashMap<>(2048);

    private final Lock[] locks;

    public Impl_Synchronized_Segment() {
        locks = new Lock[2 << 4];
        for (int i = 0; i < 2 << 4; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    @Override
    public Lock allocate(String name) {
        Lock lock = locks[index(name)];
        synchronized (lock) {
            Binary<Lock, AtomicInteger> lockBinary = lockMap
                .computeIfAbsent(name, s -> new Binary<>(new ReentrantLock(), new AtomicInteger(0)));
            lockBinary.getF2().getAndIncrement();
            return lockBinary.getF1();
        }
    }

    @Override
    public void release(String name) {
        Lock lock = locks[index(name)];
        synchronized (lock) {
            Binary<Lock, AtomicInteger> lockBinary = lockMap.get(name);
            if (lockBinary == null) {
                String msg = String.format("锁 [%s] 不存在！", name);
                throw new ConcurrentException(msg);
            }
            int i = lockBinary.getF2().decrementAndGet();
            if (i == 0) {
                lockMap.remove(name);
            }
        }
    }

    private int index(String name) {
        return name.hashCode() & ((2 << 4) - 1);
    }

    public Map<String, Binary<Lock, AtomicInteger>> getLockMap() {
        return lockMap;
    }

}
