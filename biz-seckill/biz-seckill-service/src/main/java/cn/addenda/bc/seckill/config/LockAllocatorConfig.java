package cn.addenda.bc.seckill.config;

import cn.addenda.bc.bc.jc.allocator.lock.ReentrantLockAllocator;
import cn.addenda.bc.bc.rc.allocator.RedissonLockAllocator;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2022/12/2 18:52
 */
@Configuration
public class LockAllocatorConfig {

    @Bean
    public ReentrantLockAllocator reentrantLockAllocator() {
        return new ReentrantLockAllocator();
    }

    @Bean
    public RedissonLockAllocator redissonLockAllocator(RedissonClient redissonClient) {
        return new RedissonLockAllocator(redissonClient);
    }

}
