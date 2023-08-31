package cn.addenda.bc.bc.jc.lockallocation;


import cn.addenda.bc.bc.jc.concurrent.allocator.ReentrantLockAllocator;
import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class Test_ReentrantLockAllocator extends ReentrantLockAllocatorBaseTest {

    public Test_ReentrantLockAllocator() {
        super(new ReentrantLockAllocator());
    }

    @Test
    public void test() {
        // avg : 28
        baseTest();
    }

}
