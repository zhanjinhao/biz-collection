package cn.addenda.bc.bc.jc.allocator;

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
import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/5/30 22:51
 */
@ToString(exclude = {"lockFactory"})
public abstract class ReferenceCountAllocator<T> implements Allocator<T> {

    private final Map<String, Binary<T, AtomicInteger>> map = new ConcurrentHashMap<>();

    private final LockFactory<String> lockFactory;

    @Getter
    private final int segmentSize;

    protected ReferenceCountAllocator(int segmentSize) {
        this.segmentSize = segmentSize;
        lockFactory = new ReentrantSegmentLockFactory(segmentSize);
    }

    protected ReferenceCountAllocator() {
        this(2 << 5);
    }

    @Override
    public T allocate(String name) {
        Lock lock = lockFactory.getLock(name);
        lock.lock();
        try {
            Binary<T, AtomicInteger> binary = map
                .computeIfAbsent(name, s -> new Binary<>(referenceSupplier().get(), new AtomicInteger(0)));
            binary.getF2().getAndIncrement();
            return binary.getF1();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void release(String name) {
        Lock lock = lockFactory.getLock(name);
        lock.lock();
        try {
            Binary<T, AtomicInteger> binary = map.get(name);
            if (binary == null) {
                String msg = String.format("资源 [%s] 不存在！", name);
                throw new ConcurrentException(msg);
            }
            int i = binary.getF2().decrementAndGet();
            if (i == 0) {
                i = binary.getF2().get();
                if (i == 0) {
                    map.remove(name);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    protected abstract Supplier<T> referenceSupplier();

}
