package cn.addenda.bc.bc.jc.concurrent;

import lombok.ToString;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分段锁使用时需要保证临界区内不能同时修改状态。如下面这段代码的test方法并发执行是存在问题的：
 *
 * <pre>{@code
 *  private int i = 0;
 *
 *  public void test() {
 *      LockFactory lf = new ReentrantSegmentLockFactory(2 << 5);
 *      Lock lock = lf.getLock(1);
 *      lock.lock();
 *      try {
 *          i++;
 *      } finally {
 *          lock.unlock();
 *      }
 *  }
 * }
 * </pre>
 * @author addenda
 * @since 2023/6/4 16:30
 */
@ToString
public class ReentrantSegmentLockFactory implements LockFactory<String> {

    private final int segmentSize;

    private final Lock[] locks;

    public ReentrantSegmentLockFactory(int segmentSize) {
        if ((segmentSize & (segmentSize - 1)) != 0) {
            String msg = String.format("segmentSize 必须是2的幂，当前是：[%s]", segmentSize);
            throw new ConcurrentException(msg);
        }
        this.segmentSize = segmentSize;
        locks = new ReentrantLock[segmentSize];
        for (int i = 0; i < segmentSize; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    @Override
    public Lock getLock(String k) {
        return locks[index(k)];
    }

    private int index(String name) {
        return name.hashCode() & (segmentSize - 1);
    }

}
