package cn.addenda.bc.bc.jc.ratelimiter.tryacquire;

import cn.addenda.bc.bc.jc.ratelimiter.RequestIntervalRateLimiter;
import org.junit.Test;

/**
 * @author addenda
 * @since 2022/12/28 17:13
 */
public class RequestIntervalRateLimiterTest {

    @Test
    public void test1() throws Exception {
        RequestIntervalRateLimiter requestIntervalRateLimiter = new RequestIntervalRateLimiter(10d);
        new RateLimiterBaseTest(requestIntervalRateLimiter).test(true);
    }

}
