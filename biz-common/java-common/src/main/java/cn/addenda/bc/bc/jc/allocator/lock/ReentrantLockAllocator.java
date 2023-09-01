package cn.addenda.bc.bc.jc.allocator.lock;

import cn.addenda.bc.bc.jc.allocator.ReferenceCountAllocator;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/5/30 22:51
 */
public class ReentrantLockAllocator extends ReferenceCountAllocator<ReentrantLock> implements LockAllocator<ReentrantLock> {

    @Override
    protected Supplier<ReentrantLock> referenceSupplier() {
        return ReentrantLock::new;
    }
}
