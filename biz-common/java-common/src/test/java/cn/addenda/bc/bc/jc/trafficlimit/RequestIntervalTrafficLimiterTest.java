package cn.addenda.bc.bc.jc.trafficlimit;

import org.junit.Test;

/**
 * @author addenda
 * @since 2022/12/28 17:13
 */
public class RequestIntervalTrafficLimiterTest {

    @Test
    public void test1() throws Exception {
        RequestIntervalTrafficLimiter requestIntervalTrafficLimiter = new RequestIntervalTrafficLimiter(10d);
        new TrafficLimiterBaseTest(requestIntervalTrafficLimiter).test(true);
    }

}
