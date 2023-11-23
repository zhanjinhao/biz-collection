package cn.addenda.bc.flightschedule.config;

import cn.addenda.bc.bc.sc.springcontext.ValueResolverHelper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(ValueResolverHelper valueResolverHelper) {
        // 配置
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"
                        + valueResolverHelper.resolveFromContext("${redis.host}")
                        + ":"
                        + valueResolverHelper.resolveFromContext("${redis.port}"))
                .setPassword(valueResolverHelper.resolveFromContext("${redis.password}"));
        // 创建RedissonClient对象
        return Redisson.create(config);
    }

}
