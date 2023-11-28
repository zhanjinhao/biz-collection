package cn.addenda.bc.shortlink;

import cn.addenda.bc.bc.sc.argreslog.EnableArgResLog;
import cn.addenda.bc.bc.sc.lock.EnableLockManagement;
import cn.addenda.bc.bc.sc.multidatasource.EnableMultiDataSource;
import cn.addenda.bc.bc.sc.ratelimitation.EnableRateLimitation;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author addenda
 * @since 2022/12/7 18:15
 */
@EnableRateLimitation(namespace = "short-link-service", order = Ordered.LOWEST_PRECEDENCE - 50)
@EnableLockManagement(namespace = "short-link-service", order = Ordered.LOWEST_PRECEDENCE - 60)
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 70)
@EnableMultiDataSource(order = Ordered.LOWEST_PRECEDENCE - 80)
@EnableArgResLog(order = Ordered.LOWEST_PRECEDENCE - 90)
@MapperScan("cn.addenda.bc.shortlink.mapper")
@SpringBootApplication
public class BizShortLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(BizShortLinkApplication.class, args);
    }

}
