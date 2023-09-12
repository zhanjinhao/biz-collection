package cn.addenda.bc.bc.jc.ratelimiter.tryacquire;

import cn.addenda.bc.bc.jc.ratelimiter.GuavaRateLimiterWrapper;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2022/12/29 19:14
 */
public class GuavaRateLimiterTest {

    public static void main(String[] args) throws Exception {
        GuavaRateLimiterWrapper guavaRateLimiterWrapper = new GuavaRateLimiterWrapper(10, 1, TimeUnit.SECONDS);
        new RateLimiterBaseTest(guavaRateLimiterWrapper).test(true);
    }

}
