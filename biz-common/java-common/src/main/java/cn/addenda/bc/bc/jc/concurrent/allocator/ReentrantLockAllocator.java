package cn.addenda.bc.bc.jc.concurrent.allocator;

import cn.addenda.bc.bc.jc.concurrent.ConcurrentException;
import cn.addenda.bc.bc.jc.concurrent.LockFactory;
import cn.addenda.bc.bc.jc.concurrent.ReentrantSegmentLockFactory;
import cn.addenda.bc.bc.jc.pojo.Binary;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author addenda
 * @since 2023/5/30 22:51
 */
@ToString(exclude = {"lockFactory"})
public class ReentrantLockAllocator implements LockAllocator<ReentrantLock> {

    private final Map<String, Binary<ReentrantLock, AtomicInteger>> lockMap = new ConcurrentHashMap<>();

    private final LockFactory<String> lockFactory;

    @Getter
    private final int segmentSize;

    public ReentrantLockAllocator(int segmentSize) {
        this.segmentSize = segmentSize;
        lockFactory = new ReentrantSegmentLockFactory(segmentSize);
    }

    public ReentrantLockAllocator() {
        this(2 << 5);
    }

    @Override
    public ReentrantLock allocateLock(String name) {
        Lock lock = lockFactory.getLock(name);
        lock.lock();
        try {
            Binary<ReentrantLock, AtomicInteger> lockBinary = lockMap
                    .computeIfAbsent(name, s -> new Binary<>(new ReentrantLock(), new AtomicInteger(0)));
            lockBinary.getF2().getAndIncrement();
            return lockBinary.getF1();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseLock(String name) {
        Lock lock = lockFactory.getLock(name);
        lock.lock();
        try {
            Binary<ReentrantLock, AtomicInteger> lockBinary = lockMap.get(name);
            if (lockBinary == null) {
                String msg = String.format("锁 [%s] 不存在！", name);
                throw new ConcurrentException(msg);
            }
            int i = lockBinary.getF2().decrementAndGet();
            if (i == 0) {
                i = lockBinary.getF2().get();
                if (i == 0) {
                    lockMap.remove(name);
                }
            }
        } finally {
            lock.unlock();
        }
    }

}
