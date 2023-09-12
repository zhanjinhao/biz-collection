package cn.addenda.bc.bc.jc.ratelimiter.timeout;

import cn.addenda.bc.bc.jc.ratelimiter.RequestLogRateLimiter;

/**
 * @author addenda
 * @since 2022/12/28 14:43
 */
public class RequestLogRateLimiterTimeoutTest {

    public static void main(String[] args) throws Exception {
        RequestLogRateLimiter requestLogRateLimiter = new RequestLogRateLimiter(10, 1000);
        new RateLimiterTimeoutBaseTest(requestLogRateLimiter).test(true);
    }

}
