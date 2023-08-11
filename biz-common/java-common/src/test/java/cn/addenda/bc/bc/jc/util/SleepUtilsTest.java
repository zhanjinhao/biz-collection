package cn.addenda.bc.bc.jc.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/3/9 19:45
 */
@Slf4j
public class SleepUtilsTest {

    @Test
    public void main() {
        Thread thread = new Thread(() -> {
            log.info("start. ");
            SleepUtils.sleep(TimeUnit.SECONDS, 30, false);
            log.info("end. ");
            if (Thread.currentThread().isInterrupted()) {
                log.info("睡眠期间被打断了！");
            }
        });
        thread.start();

        while (thread.isAlive()) {
            SleepUtils.sleep(TimeUnit.SECONDS, 3, false);
            thread.interrupt();
        }
    }

}
