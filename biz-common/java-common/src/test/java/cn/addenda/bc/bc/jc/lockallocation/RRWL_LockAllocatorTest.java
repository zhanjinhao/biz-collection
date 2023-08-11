package cn.addenda.bc.bc.jc.lockallocation;


import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class RRWL_LockAllocatorTest extends ReentrantLockAllocatorBaseTest {

    public RRWL_LockAllocatorTest() {
        super(new ReentrantLockAllocator_ReentrantReadWriteLock());
    }

    @Test
    public void test() {
        // avg : 384
        main();
    }

}
