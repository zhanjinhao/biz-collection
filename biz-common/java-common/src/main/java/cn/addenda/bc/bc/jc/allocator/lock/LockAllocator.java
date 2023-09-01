package cn.addenda.bc.bc.jc.allocator.lock;

import cn.addenda.bc.bc.jc.allocator.Allocator;

import java.util.concurrent.locks.Lock;

/**
 * {@link LockAllocator#allocate(String)}动态的获取与name绑定的锁。保证整个JVM在同一个时间点上多个线程能获取到与name绑定的同一把锁。
 *
 * @author addenda
 * @since 2023/6/4 16:30
 */
public interface LockAllocator<T extends Lock> extends Allocator<T> {

    T allocate(String name);

    void release(String name);

}
