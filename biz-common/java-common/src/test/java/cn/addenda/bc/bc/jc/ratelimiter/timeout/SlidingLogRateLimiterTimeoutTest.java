package cn.addenda.bc.bc.jc.ratelimiter.timeout;

import cn.addenda.bc.bc.jc.ratelimiter.SlidingLogRateLimiter;

/**
 * @author addenda
 * @since 2022/12/28 14:14
 */
public class SlidingLogRateLimiterTimeoutTest {

    public static void main(String[] args) throws Exception {
        SlidingLogRateLimiter slidingLogRateLimiter = new SlidingLogRateLimiter(10, 1000);
        new RateLimiterTimeoutBaseTest(slidingLogRateLimiter).test(true);
    }

}
