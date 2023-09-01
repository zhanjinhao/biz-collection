package cn.addenda.bc.bc.jc.allocator.trafficlimit;

import cn.addenda.bc.bc.jc.allocator.ReferenceCountAllocator;
import cn.addenda.bc.bc.jc.trafficlimit.TokenBucketTrafficLimiter;

import java.util.function.Supplier;

/**
 * @author addenda
 * @since 2023/9/1 9:06
 */
public class TokenBucketTrafficLimiterAllocator extends ReferenceCountAllocator<TokenBucketTrafficLimiter> implements TrafficLimiterAllocator<TokenBucketTrafficLimiter> {

    private final long capacity;

    private final double permitsPerSecond;

    public TokenBucketTrafficLimiterAllocator(int segmentSize, long capacity, double permitsPerSecond) {
        super(segmentSize);
        this.capacity = capacity;
        this.permitsPerSecond = permitsPerSecond;
    }

    public TokenBucketTrafficLimiterAllocator(long capacity, double permitsPerSecond) {
        this.capacity = capacity;
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    protected Supplier<TokenBucketTrafficLimiter> referenceSupplier() {
        return () -> new TokenBucketTrafficLimiter(capacity, permitsPerSecond);
    }

}
