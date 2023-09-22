package cn.addenda.bc.seckill.config;

import cn.addenda.bc.bc.rc.allocator.RedissonLockAllocator;
import cn.addenda.bc.bc.sc.lock.LockConfigurer;
import cn.addenda.bc.bc.sc.lock.LockHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2022/11/30 19:43
 */
@Configuration
public class LockConfig {

    @Bean
    public LockConfigurer lockConfigurer(RedissonLockAllocator redissonLockAllocator) {
        return new LockConfigurer(redissonLockAllocator);
    }

    @Bean
    public LockHelper lockHelper(RedissonLockAllocator redissonLockHelper) {
        return new LockHelper("biz-seckill", redissonLockHelper);
    }

}
