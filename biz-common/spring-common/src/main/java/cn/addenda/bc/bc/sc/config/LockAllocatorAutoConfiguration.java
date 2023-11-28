package cn.addenda.bc.bc.sc.config;

import cn.addenda.bc.bc.jc.allocator.lock.ReentrantLockAllocator;
import cn.addenda.bc.bc.rc.allocator.RedissonLockAllocator;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2022/12/2 18:52
 */
@Configuration
public class LockAllocatorAutoConfiguration {

    @ConditionalOnMissingBean(value = ReentrantLockAllocator.class)
    @Bean
    public ReentrantLockAllocator reentrantLockAllocator() {
        return new ReentrantLockAllocator();
    }

    @ConditionalOnMissingBean(RedissonLockAllocator.class)
    @Bean
    public RedissonLockAllocator redissonLockAllocator(RedissonClient redissonClient) {
        return new RedissonLockAllocator(redissonClient);
    }

}
