package cn.addenda.bc.bc.rc;

import cn.addenda.bc.bc.rc.ratelimiter.RedissonRateLimiterWrapper;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

/**
 * @author addenda
 * @since 2023/9/11 18:29
 */
public class RedissonRateLimiterTimeoutTest {

    @Test
    public void test() throws IOException {

        String path = Objects.requireNonNull(RedissonRateLimiterTimeoutTest.class.getClassLoader()
            .getResource("cn/addenda/bc/bc/rc/redis.properties")).getPath();

        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(path)));

        // 配置
        Config config = new Config();
        config.useSingleServer()
            .setAddress("redis://" + properties.get("host") + ":" + properties.get("port"))
            .setPassword((String) properties.get("password"));
        // 创建RedissonClient对象
        RedissonClient redissonClient = Redisson.create(config);

        RRateLimiter rateLimiter = redissonClient.getRateLimiter("java-common:ratelimitertest");
        rateLimiter.trySetRate(RateType.OVERALL, 10, 1, RateIntervalUnit.SECONDS);
        RedissonRateLimiterWrapper redissonRateLimiterWrapper = new RedissonRateLimiterWrapper(rateLimiter);
        new RateLimiterTimeoutBaseTest(redissonRateLimiterWrapper).test(true);
    }
}
