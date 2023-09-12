package cn.addenda.bc.bc.jc.ratelimiter.tryacquire;

import cn.addenda.bc.bc.jc.ratelimiter.SlidingLogRateLimiter;

/**
 * @author addenda
 * @since 2022/12/28 14:14
 */
public class SlidingLogRateLimiterTest {

    public static void main(String[] args) throws Exception {
        SlidingLogRateLimiter slidingLogRateLimiter = new SlidingLogRateLimiter(10, 1000);
        new RateLimiterBaseTest(slidingLogRateLimiter).test(true);
    }

}
