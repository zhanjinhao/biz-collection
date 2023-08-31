package cn.addenda.bc.bc.jc.lockallocation;


import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class Test_ReentrantReadWriteLock_Segment extends ReentrantLockAllocatorBaseTest {

    public Test_ReentrantReadWriteLock_Segment() {
        super(new Impl_ReentrantReadWriteLock_Segment());
    }

    @Test
    public void test() {
        // avg : 75
        baseTest();
    }

}
