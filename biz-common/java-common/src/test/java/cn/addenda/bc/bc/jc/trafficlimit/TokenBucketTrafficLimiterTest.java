package cn.addenda.bc.bc.jc.trafficlimit;

/**
 * @author addenda
 * @since 2022/12/29 19:14
 */
public class TokenBucketTrafficLimiterTest {

    public static void main(String[] args) throws Exception {
        double d = 1.9d;
        System.out.println((long) d);

        TokenBucketTrafficLimiter tokenBucketTrafficLimiter = new TokenBucketTrafficLimiter(10, 10);
        new TrafficLimiterBaseTest(tokenBucketTrafficLimiter).test(true);
    }

}
