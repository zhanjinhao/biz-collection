package cn.addenda.bc.bc.jc.trafficlimit;

/**
 * @author addenda
 * @since 2022/12/28 14:14
 */
public class SlidingWindowTrafficLimiterTest {

    public static void main(String[] args) throws Exception {
        SlidingWindowTrafficLimiter slidingWindowTrafficLimiter = new SlidingWindowTrafficLimiter(10, 1000, 100, false);
        new TrafficLimiterBaseTest(slidingWindowTrafficLimiter).test(true);
    }

}
