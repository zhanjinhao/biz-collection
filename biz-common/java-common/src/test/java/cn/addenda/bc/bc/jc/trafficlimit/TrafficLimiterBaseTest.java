package cn.addenda.bc.bc.jc.trafficlimit;

import cn.addenda.bc.bc.jc.util.SleepUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author addenda
 * @since 2022/12/28 18:59
 */
public class TrafficLimiterBaseTest {

    SecureRandom r = new SecureRandom();

    TrafficLimiter trafficLimiter;

    public TrafficLimiterBaseTest(TrafficLimiter trafficLimiter) {
        this.trafficLimiter = trafficLimiter;
    }

    public void test(boolean outputAcquireSuccess) {
        AtomicLong acquireTimes = new AtomicLong(0L);
        AtomicLong passTimes = new AtomicLong(0L);
        List<Thread> threadList = new ArrayList<>();
        BlockingQueue<Long> blockingQueue = new LinkedBlockingDeque<>();
        for (int i = 0; i < 100; i++) {
            threadList.add(new Thread(() -> {
                while (true) {
                    boolean b = trafficLimiter.acquire();
                    acquireTimes.incrementAndGet();
                    if (b) {
                        blockingQueue.offer(System.currentTimeMillis());
                        passTimes.incrementAndGet();
                    }
                    try {
                        Thread.sleep(r.nextInt(50) + 50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
        }

        if (outputAcquireSuccess) {
            new Thread(() -> {
                while (true) {
                    try {
                        Long take = blockingQueue.take();
                        System.out.println(take);
                    } catch (InterruptedException e) {

                    }
                }
            }).start();
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        while (true) {
            SleepUtils.sleep(TimeUnit.SECONDS, 100);
            System.out.println("acquireTimes  : " + acquireTimes.get());
            System.out.println("passTimes     : " + passTimes.get());
        }
    }
}
