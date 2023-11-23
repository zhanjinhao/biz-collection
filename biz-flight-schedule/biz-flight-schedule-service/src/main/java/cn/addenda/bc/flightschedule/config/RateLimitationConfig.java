package cn.addenda.bc.flightschedule.config;

import cn.addenda.bc.bc.jc.ratelimiter.RateLimiter;
import cn.addenda.bc.bc.rc.allocator.ExternallyConfiguredRedissonRateLimiterAllocator;
import cn.addenda.bc.bc.rc.allocator.NamedExpiredAllocator;
import cn.addenda.bc.bc.sc.ratelimitation.RateLimitationHelper;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author addenda
 * @since 2023/9/28 20:40
 */
@Configuration
public class RateLimitationConfig {

    @Bean
    public ExternallyConfiguredRedissonRateLimiterAllocator seckillRateLimiterAllocator(RedissonClient redissonClient) {
        ExternallyConfiguredRedissonRateLimiterAllocator allocator =
            new ExternallyConfiguredRedissonRateLimiterAllocator("biz-flight-schedule:flight-schedule", redissonClient);
        allocator.setRate(RateType.OVERALL, 10, 10000);
        return allocator;
    }

    @Bean
    public RateLimitationHelper rateLimitationHelper(List<? extends NamedExpiredAllocator<? extends RateLimiter>> rateLimiterList) {
        return new RateLimitationHelper("biz-flight-schedule", rateLimiterList);
    }

}
