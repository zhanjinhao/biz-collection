package cn.addenda.bc.bc.jc.allocator.ratelimiter;

import cn.addenda.bc.bc.jc.allocator.ReferenceCountAllocator;
import cn.addenda.bc.bc.jc.ratelimiter.TokenBucketRateLimiter;

import java.util.function.Function;

/**
 * @author addenda
 * @since 2023/9/1 9:06
 */
public class TokenBucketRateLimiterAllocator extends ReferenceCountAllocator<TokenBucketRateLimiter> implements RateLimiterAllocator<TokenBucketRateLimiter> {

    private final long capacity;

    private final long permitsPerSecond;

    public TokenBucketRateLimiterAllocator(int segmentSize, long capacity, long permitsPerSecond) {
        super(segmentSize);
        this.capacity = capacity;
        this.permitsPerSecond = permitsPerSecond;
    }

    public TokenBucketRateLimiterAllocator(long capacity, long permitsPerSecond) {
        this.capacity = capacity;
        this.permitsPerSecond = permitsPerSecond;
    }

    @Override
    protected Function<String, TokenBucketRateLimiter> referenceFunction() {
        return new Function<String, TokenBucketRateLimiter>() {
            @Override
            public TokenBucketRateLimiter apply(String s) {
                return new TokenBucketRateLimiter(capacity, permitsPerSecond);
            }
        };
    }
}
