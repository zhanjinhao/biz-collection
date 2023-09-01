package cn.addenda.bc.bc.sc.lock;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.allocator.lock.LockAllocator;
import cn.addenda.bc.bc.jc.function.TRunnable;
import cn.addenda.bc.bc.jc.function.TSupplier;
import cn.addenda.bc.bc.jc.util.ExceptionUtil;

import java.util.concurrent.locks.Lock;

/**
 * @author addenda
 * @since 2022/12/1 18:55
 */
public class LockHelper extends LockAspectSupport {

    public static final String SYSTEM_BUSY = "系统繁忙，请稍后重试！";

    public LockHelper(String namespace, LockAllocator<? extends Lock> lockAllocator) {
        this.setNamespace(namespace);
        this.setLockAllocator(lockAllocator);
    }

    /**
     * 最简单的加锁场景，arguments[0] 是 key
     */
    public <R> R doLock(TSupplier<R> supplier, Object... arguments) {
        LockedAttr attr = LockedAttr.builder().build();
        return doLock(attr, supplier, arguments);
    }

    public void doLock(TRunnable runnable, Object... arguments) {
        LockedAttr attr = LockedAttr.builder().build();
        doLock(attr, runnable, arguments);
    }

    /**
     * 较上一个场景，arguments[0] 是 key，prefix可以指定
     */
    public <R> R doLock(String prefix, TSupplier<R> supplier, Object... arguments) {
        LockedAttr attr = LockedAttr.builder().prefix(prefix).build();
        return doLock(attr, supplier, arguments);
    }

    public void doLock(String prefix, TRunnable runnable, Object... arguments) {
        LockedAttr attr = LockedAttr.builder().prefix(prefix).build();
        doLock(attr, runnable, arguments);
    }

    /**
     * 较上一个场景，arguments[0] 是 key，lockFailedMsg和prefix可以指定
     */
    public <R> R doLock(String lockFailedMsg, String prefix, TSupplier<R> supplier, Object... arguments) {
        LockedAttr attr = LockedAttr.builder().prefix(prefix).lockFailedMsg(lockFailedMsg).build();
        return doLock(attr, supplier, arguments);
    }

    public void doLock(String lockFailedMsg, String prefix, TRunnable runnable, Object... arguments) {
        LockedAttr attr = LockedAttr.builder().prefix(prefix).lockFailedMsg(lockFailedMsg).build();
        doLock(attr, runnable, arguments);
    }

    public void doLock(LockedAttr attr, TRunnable runnable, Object... arguments) {
        doLock(attr, runnable.toTSupplier(), arguments);
    }

    public <R> R doLock(LockedAttr attr, TSupplier<R> supplier, Object... arguments) {
        if (arguments == null || arguments.length == 0 || attr == null || supplier == null) {
            throw new LockException("参数不能为空！");
        }
        try {
            return (R) invokeWithinLock(attr, arguments, supplier::get, null);
        } catch (Throwable throwable) {
            ExceptionUtil.reportAsRuntimeException(throwable, LockException.class);
            throw new ServiceException("系统异常");
        }
    }

}
