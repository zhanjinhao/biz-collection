package cn.addenda.bc.bc.sc.lock;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.concurrent.allocator.LockAllocator;
import cn.addenda.bc.bc.jc.function.TSupplier;
import cn.addenda.bc.bc.sc.util.SpELUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

/**
 * @author addenda
 * @since 2022/12/1 18:47
 */
@Slf4j
public class LockAspectSupport {

    @Setter
    @Getter
    private String namespace = "default";

    @Setter
    private LockAllocator<? extends Lock> lockAllocator;

    @Setter
    protected String spELArgsName = "spELArgs";

    public Object invokeWithinLock(LockedAttr lockedAttr, Object[] arguments, TSupplier<Object> supplier, Method method) throws Throwable {

        if (lockAllocator == null) {
            throw new LockException("LockAllocator 不能为空！");
        }

        String spEL = lockedAttr.getSpEL();
        String key = SpELUtils.getKey(spEL, method, spELArgsName, arguments);

        if (key == null || key.length() == 0) {
            throw new LockException("不能对 null 或 空字符串 加锁。");
        }

        String lockedKey = namespace + ":" + lockedAttr.getPrefix() + ":" + key + ":lock";
        Lock lock = lockAllocator.allocateLock(lockedKey);
        try {
            // Lock类有四个加锁方法：
            //  1.lock()
            //  2.lockInterruptibly()
            //  3.tryLock()
            //  4.tryLock(long time, TimeUnit unit)
            // 采用4加锁的原因是：
            //  1.前两个方法获取不到锁会一直等待，但在业务开发中。如果长时间获取不到锁应该向上抛出异常，
            //    进而提醒研发系统有问题（死锁或性能问题），而不是等待tomcat/jetty线程池的线程都耗尽。
            //  2.tryLock如果获取不到锁会立刻返回false，如果系统短时并发较多（如短时间来了100个请求，
            //    每个请求执行500ms，网关超时时间是1分钟），那么系统会在能承受的了情况下对外报异常。
            // Redisson提供的RLock里，增加了三个加锁方法：
            //  1.lockInterruptibly(long leaseTime, TimeUnit unit)
            //  2.lock(long leaseTime, TimeUnit unit)
            //  3.tryLock(long waitTime, long leaseTime, TimeUnit unit)
            // 这三个方法中，1和2的问题也是获取不到锁时会一直等待。而3会在加锁之后租约到期时自动释放，
            // 自动释放存在脏数据的问题，OLTP系统中，脏数据是要被绝对避免的。
            if (lock.tryLock(lockedAttr.getWaitTime(), lockedAttr.getTimeUnit())) {
                try {
                    log.debug("锁 [{}] 加锁成功。", lockedKey);
                    return supplier.get();
                } finally {
                    lock.unlock();
                    log.debug("锁 [{}] 释放成功。", lockedKey);
                }
            } else {
                throw new ServiceException(lockedAttr.getLockFailedMsg().replace("{}", lockedKey));
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            String msg = String.format("锁 [%s] 获取期间被中断。", lockedKey);
            throw new LockException(msg, interruptedException);
        }

    }

}
