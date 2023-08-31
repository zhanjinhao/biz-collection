package cn.addenda.bc.bc.jc.lockallocation;

import cn.addenda.bc.bc.jc.concurrent.ConcurrentException;
import cn.addenda.bc.bc.jc.concurrent.allocator.LockAllocator;
import cn.addenda.bc.bc.jc.pojo.Binary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author addenda
 * @since 2023/5/30 22:51
 */
public class Impl_ReentrantReadWriteLock implements LockAllocator<Lock> {

    private final Map<String, Binary<Lock, AtomicInteger>> lockMap = new ConcurrentHashMap<>();

    private final ReadWriteLock lock;

    public Impl_ReentrantReadWriteLock(boolean fair) {
        lock = new ReentrantReadWriteLock(fair);
    }

    @Override
    public Lock allocateLock(String name) {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            Binary<Lock, AtomicInteger> lockBinary = lockMap
                .computeIfAbsent(name, s -> new Binary<>(new ReentrantLock(), new AtomicInteger(0)));
            lockBinary.getF2().getAndIncrement();
            return lockBinary.getF1();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void releaseLock(String name) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            Binary<Lock, AtomicInteger> lockBinary = lockMap.get(name);
            if (lockBinary == null) {
                String msg = String.format("锁 [%s] 不存在！", name);
                throw new ConcurrentException(msg);
            }
            int i = lockBinary.getF2().decrementAndGet();
            if (i == 0) {
                lockMap.remove(name);
            }
        } finally {
            writeLock.unlock();
        }
    }

}
