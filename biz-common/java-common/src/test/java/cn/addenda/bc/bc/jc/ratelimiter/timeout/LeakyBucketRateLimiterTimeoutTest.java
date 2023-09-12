package cn.addenda.bc.bc.jc.ratelimiter.timeout;

import cn.addenda.bc.bc.jc.ratelimiter.LeakyBucketRateLimiter;
import org.junit.Test;

/**
 * @author addenda
 * @since 2022/12/28 17:13
 */
public class LeakyBucketRateLimiterTimeoutTest {

    @Test
    public void test1() throws Exception {
        LeakyBucketRateLimiter leakyBucketRateLimiter = new LeakyBucketRateLimiter(4000L, 10);
        new RateLimiterTimeoutBaseTest(leakyBucketRateLimiter).test(true);
    }

}
