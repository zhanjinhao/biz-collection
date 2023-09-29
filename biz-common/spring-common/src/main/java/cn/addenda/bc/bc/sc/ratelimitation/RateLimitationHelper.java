package cn.addenda.bc.bc.sc.ratelimitation;

import cn.addenda.bc.bc.jc.function.TRunnable;
import cn.addenda.bc.bc.jc.function.TSupplier;
import cn.addenda.bc.bc.jc.ratelimiter.RateLimiter;
import cn.addenda.bc.bc.jc.util.ExceptionUtil;
import cn.addenda.bc.bc.rc.allocator.NamedExpiredAllocator;

import java.util.List;

/**
 * @author addenda
 * @since 2023/9/28 20:28
 */
public class RateLimitationHelper extends RateLimitationSupport {

    public RateLimitationHelper(List<NamedExpiredAllocator<? extends RateLimiter>> rateLimiterList) {
        super(rateLimiterList);
    }

    public RateLimitationHelper(String namespace, List<? extends NamedExpiredAllocator<? extends RateLimiter>> rateLimiterList) {
        super(rateLimiterList);
        this.setNamespace(namespace);
    }

    /**
     * 最简单的加锁场景，arguments[0] 是 key
     */
    public <R> R rateLimit(String rateLimiterAllocator, TSupplier<R> supplier, Object... arguments) {
        RateLimitationAttr attr = RateLimitationAttr.builder().rateLimiterAllocator(rateLimiterAllocator).build();
        return rateLimit(attr, supplier, arguments);
    }

    public void rateLimit(String rateLimiterAllocator, TRunnable runnable, Object... arguments) {
        RateLimitationAttr attr = RateLimitationAttr.builder().rateLimiterAllocator(rateLimiterAllocator).build();
        rateLimit(attr, runnable, arguments);
    }

    /**
     * 较上一个场景，arguments[0] 是 key，prefix可以指定
     */
    public <R> R rateLimit(String rateLimiterAllocator, String prefix, TSupplier<R> supplier, Object... arguments) {
        RateLimitationAttr attr = RateLimitationAttr.builder()
            .rateLimiterAllocator(rateLimiterAllocator)
            .prefix(prefix).build();
        return rateLimit(attr, supplier, arguments);
    }

    public void rateLimit(String rateLimiterAllocator, String prefix, TRunnable runnable, Object... arguments) {
        RateLimitationAttr attr = RateLimitationAttr.builder()
            .rateLimiterAllocator(rateLimiterAllocator)
            .prefix(prefix).build();
        rateLimit(attr, runnable, arguments);
    }

    public void rateLimit(RateLimitationAttr attr, TRunnable runnable, Object... arguments) {
        rateLimit(attr, runnable.toTSupplier(), arguments);
    }

    public <R> R rateLimit(RateLimitationAttr attr, TSupplier<R> supplier, Object... arguments) {
        if (arguments == null || arguments.length == 0 || attr == null || supplier == null) {
            throw new RateLimitationException("参数不能为空！");
        }
        try {
            return (R) invokeWithinRateLimitation(attr, arguments, supplier::get, null);
        } catch (Throwable throwable) {
            throw ExceptionUtil.wrapAsRuntimeException(throwable, RateLimitationException.class);
        }
    }

}
