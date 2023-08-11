package cn.addenda.bc.bc.jc.lockallocation;


import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class RLS_LockAllocatorTest extends ReentrantLockAllocatorBaseTest {

    public RLS_LockAllocatorTest() {
        super(new ReentrantLockAllocator_ReentrantLock_Segment());
    }

    @Test
    public void test() {
        // avg : 28
        main();
    }

}
