package cn.addenda.bc.bc.jc.lockallocation;


import cn.addenda.bc.bc.jc.concurrent.allocator.ReentrantLockAllocator;
import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class ReentrantLockAllocatorTest extends ReentrantLockAllocatorBaseTest {

    public ReentrantLockAllocatorTest() {
        super(new ReentrantLockAllocator());
    }

    @Test
    public void test() {
        // avg : 28
        main();
    }

}
