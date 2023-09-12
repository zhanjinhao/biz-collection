package cn.addenda.bc.bc.jc.ratelimiter.tryacquire;

import cn.addenda.bc.bc.jc.ratelimiter.SlidingWindowRateLimiter;

/**
 * @author addenda
 * @since 2022/12/28 14:14
 */
public class SlidingWindowRateLimiterTest {

    public static void main(String[] args) throws Exception {
        SlidingWindowRateLimiter slidingWindowRateLimiter = new SlidingWindowRateLimiter(10, 1000, 100, false);
        new RateLimiterBaseTest(slidingWindowRateLimiter).test(true);
    }

}
