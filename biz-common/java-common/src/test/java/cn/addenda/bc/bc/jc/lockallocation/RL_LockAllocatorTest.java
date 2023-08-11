package cn.addenda.bc.bc.jc.lockallocation;


import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class RL_LockAllocatorTest extends ReentrantLockAllocatorBaseTest {

    public RL_LockAllocatorTest() {
        super(new ReentrantLockAllocator_ReentrantLock());
    }

    @Test
    public void test() {
        // avg: 30
        main();
    }

}
