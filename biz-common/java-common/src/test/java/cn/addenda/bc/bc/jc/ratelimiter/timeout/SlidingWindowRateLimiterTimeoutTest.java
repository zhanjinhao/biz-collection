package cn.addenda.bc.bc.jc.ratelimiter.timeout;

import cn.addenda.bc.bc.jc.ratelimiter.SlidingWindowRateLimiter;

/**
 * @author addenda
 * @since 2022/12/28 14:14
 */
public class SlidingWindowRateLimiterTimeoutTest {

    public static void main(String[] args) throws Exception {
        SlidingWindowRateLimiter slidingWindowRateLimiter = new SlidingWindowRateLimiter(10, 1000, 100, false);
        new RateLimiterTimeoutBaseTest(slidingWindowRateLimiter).test(true);
    }

}
