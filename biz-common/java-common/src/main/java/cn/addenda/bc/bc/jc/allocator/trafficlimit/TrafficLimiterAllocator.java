package cn.addenda.bc.bc.jc.allocator.trafficlimit;

import cn.addenda.bc.bc.jc.allocator.Allocator;
import cn.addenda.bc.bc.jc.trafficlimit.TrafficLimiter;

/**
 * @author addenda
 * @since 2023/9/1 9:06
 */
public interface TrafficLimiterAllocator<T extends TrafficLimiter> extends Allocator<T> {
}
