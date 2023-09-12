package cn.addenda.bc.bc.jc.ratelimiter.tryacquire;

import cn.addenda.bc.bc.jc.ratelimiter.LeakyBucketRateLimiter;
import org.junit.Test;

/**
 * @author addenda
 * @since 2022/12/28 15:14
 */
public class LeakyBucketRateLimiterTest {

    @Test
    public void test1() throws Exception {
        LeakyBucketRateLimiter leakyBucketRateLimiter = new LeakyBucketRateLimiter(200L, 1);
        new RateLimiterBaseTest(leakyBucketRateLimiter).test(true);
    }

}
