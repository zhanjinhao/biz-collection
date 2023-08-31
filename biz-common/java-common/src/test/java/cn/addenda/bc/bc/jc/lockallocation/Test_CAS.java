package cn.addenda.bc.bc.jc.lockallocation;


import org.junit.Test;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public class Test_CAS extends ReentrantLockAllocatorBaseTest {

    public Test_CAS() {
        super(new Impl_CAS());
    }

    @Test
    public void test() {
        // avg: 30
        baseTest();
    }

}
