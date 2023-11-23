package cn.addenda.bc.flightschedule.config;

import cn.addenda.bc.bc.sc.idempotence.DbStorageCenter;
import cn.addenda.bc.bc.sc.idempotence.IdempotenceHelper;
import cn.addenda.bc.bc.sc.idempotence.RedisStorageCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.sql.DataSource;

/**
 * @author addenda
 * @since 2023/9/30 10:28
 */
@Configuration
public class IdempotenceConfig {

    @Bean
    public RedisStorageCenter redisStorageCenter(StringRedisTemplate stringRedisTemplate) {
        return new RedisStorageCenter(stringRedisTemplate);
    }

//    @Bean
    public DbStorageCenter dbStorageCenter(DataSource dataSource) {
        return new DbStorageCenter(dataSource);
    }

    @Bean
    public IdempotenceHelper idempotenceHelper() {
        IdempotenceHelper idempotenceHelper = new IdempotenceHelper();
        idempotenceHelper.setNamespace("biz-flight-schedule:idempotence");
        return idempotenceHelper;
    }

}
