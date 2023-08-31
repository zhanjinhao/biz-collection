package cn.addenda.bc.bc.jc.lockallocation;


import cn.addenda.bc.bc.jc.util.SleepUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class Test_ReentrantLock extends ReentrantLockAllocatorBaseTest {

    public Test_ReentrantLock() {
        super(new Impl_ReentrantLock(true));
    }

    @Test
    public void test() {
        // options unsafe true
        // monitor java.util.concurrent.locks.AbstractQueuedSynchronizer parkAndCheckInterrupt  -n 10  --cycle 60
        SleepUtils.sleep(TimeUnit.SECONDS, 30);

        // avg: 30
        baseTest();

        SleepUtils.sleep(TimeUnit.SECONDS, 60);

    }

}
