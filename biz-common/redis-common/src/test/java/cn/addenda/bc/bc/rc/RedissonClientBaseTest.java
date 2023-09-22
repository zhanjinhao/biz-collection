package cn.addenda.bc.bc.rc;

import cn.addenda.bc.bc.rc.ratelimiter.ExpiredRedissonRateLimiterTest;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author addenda
 * @since 2023/9/21 22:34
 */
public class RedissonClientBaseTest {

    public static RedissonClient redissonClient() throws Exception {
        String path = ExpiredRedissonRateLimiterTest.class.getClassLoader()
            .getResource("cn/addenda/bc/bc/rc/redis.properties").getPath();

        Properties properties = new Properties();
        properties.load(new FileInputStream(path));

        // 配置
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://" + properties.get("host") + ":" + properties.get("port"))
            .setPassword((String) properties.get("password"));
        // 创建RedissonClient对象
        return Redisson.create(config);
    }

}
