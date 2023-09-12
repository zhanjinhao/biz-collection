package cn.addenda.bc.bc.jc.allocator.ratelimiter;

import cn.addenda.bc.bc.jc.allocator.Allocator;
import cn.addenda.bc.bc.jc.ratelimiter.RateLimiter;

/**
 * @author addenda
 * @since 2023/9/1 9:06
 */
public interface RateLimiterAllocator<T extends RateLimiter> extends Allocator<T> {
}
