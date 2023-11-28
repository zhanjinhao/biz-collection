package cn.addenda.bc.seckill.config;

import cn.addenda.bc.bc.jc.ratelimiter.RateLimiter;
import cn.addenda.bc.bc.rc.allocator.ExternallyConfiguredRedissonRateLimiterAllocator;
import cn.addenda.bc.bc.rc.allocator.NamedExpiredAllocator;
import cn.addenda.bc.bc.sc.ratelimitation.RateLimitationHelper;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @author addenda
 * @since 2023/9/28 20:40
 */
@Configuration
public class RateLimitationConfig implements EnvironmentAware {

    private Environment environment;

    @Bean
    public ExternallyConfiguredRedissonRateLimiterAllocator globalRateLimiterAllocator(RedissonClient redissonClient) {
        ExternallyConfiguredRedissonRateLimiterAllocator allocator =
            new ExternallyConfiguredRedissonRateLimiterAllocator(
                environment.resolvePlaceholders("${spring.application.name}") + ":ratelimitation:global",
                redissonClient);
        allocator.setRate(RateType.OVERALL, 10, 10000);
        return allocator;
    }

    @Bean
    public RateLimitationHelper rateLimitationHelper(List<? extends NamedExpiredAllocator<? extends RateLimiter>> rateLimiterList) {
        return new RateLimitationHelper(
            environment.resolvePlaceholders("${spring.application.name}" + ":ratelimitation"),
            rateLimiterList);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
