package cn.addenda.bc.bc.sc.config;

import cn.addenda.bc.bc.rc.allocator.RedissonLockAllocator;
import cn.addenda.bc.bc.sc.lock.LockConfigurer;
import cn.addenda.bc.bc.sc.lock.LockHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author addenda
 * @since 2022/11/30 19:43
 */
@Configuration
public class LockAutoConfiguration implements EnvironmentAware {

    private Environment environment;

    @ConditionalOnMissingBean(LockConfigurer.class)
    @Bean
    public LockConfigurer lockConfigurer(RedissonLockAllocator redissonLockAllocator) {
        return new LockConfigurer(redissonLockAllocator);
    }

    @ConditionalOnMissingBean(LockHelper.class)
    @Bean
    public LockHelper lockHelper(RedissonLockAllocator redissonLockHelper) {
        return new LockHelper(environment.resolvePlaceholders("${spring.application.name}"), redissonLockHelper);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
