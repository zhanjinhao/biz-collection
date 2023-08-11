package cn.addenda.bc.bc.jc.lockallocation;


import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class RRWLS_LockAllocatorTest extends ReentrantLockAllocatorBaseTest {

    public RRWLS_LockAllocatorTest() {
        super(new ReentrantLockAllocator_ReentrantReadWriteLock_Segment());
    }

    @Test
    public void test() {
        // avg : 75
        main();
    }

}
