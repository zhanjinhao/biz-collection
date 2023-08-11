package cn.addenda.bc.bc.jc.lockallocation;

import cn.addenda.bc.bc.jc.concurrent.allocator.LockAllocator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * @author addenda
 * @since 2023/6/3 12:36
 */
public abstract class ReentrantLockAllocatorBaseTest {

    LockAllocator<? extends Lock> lockAllocator;

    public ReentrantLockAllocatorBaseTest(LockAllocator<? extends Lock> lockAllocator) {
        this.lockAllocator = lockAllocator;
    }

    @Test
    public void main() {

        List<Long> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            test2();
            long end = System.currentTimeMillis();
            list.add(end - start);
        }
        list.sort(Comparator.naturalOrder());

        long sum = 0;
        for (int i = 10; i < 90; i++) {
            sum = list.get(i) + sum;
        }
        System.out.println("avg : " + sum / 80 + " ms");
    }

    public void test2() {

        int values[] = new int[100];
        for (int i = 0; i < 100; i++) {
            values[i] = 0;
        }

        long start = System.currentTimeMillis();

        List<Thread> threadList1 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            threadList1.add(new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    Lock test = lockAllocator.allocateLock("test" + finalI % 10);
                    test.lock();
                    try {
                        values[finalI]++;
                    } finally {
                        test.unlock();
                        lockAllocator.releaseLock("test" + finalI % 10);
                    }
                }
            }));
        }

        List<Thread> threadList2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            threadList2.add(new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    Lock test = lockAllocator.allocateLock("test" + finalI % 10);
                    test.lock();
                    try {
                        values[finalI]--;
                    } finally {
                        test.unlock();
                        lockAllocator.releaseLock("test" + finalI % 10);
                    }
                }
            }));
        }

//        for (int i = 0; i < 100; i++) {
//            threadList1.get(i).start();
//            threadList2.get(i).start();
//        }

        threadList1.forEach(t -> t.start());
        threadList2.forEach(t -> t.start());

        threadList1.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        threadList2.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        long end = System.currentTimeMillis();

        System.out.print((end - start) + "ms, [");
        for (int i : values) {
            System.out.print(i + ",");
        }
        System.out.println();

    }


}
