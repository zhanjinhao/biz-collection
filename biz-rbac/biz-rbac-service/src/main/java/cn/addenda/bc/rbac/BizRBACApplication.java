package cn.addenda.bc.rbac;

import cn.addenda.bc.bc.sc.argreslog.EnableArgResLog;
import cn.addenda.bc.bc.sc.lock.EnableLockManagement;
import cn.addenda.bc.bc.sc.multidatasource.EnableMultiDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author addenda
 * @since 2022/1/14 15:44
 */
@EnableLockManagement(namespace = "rbac-service", order = Ordered.LOWEST_PRECEDENCE - 60)
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 70)
@EnableMultiDataSource(order = Ordered.LOWEST_PRECEDENCE - 80)
@EnableArgResLog(order = Ordered.LOWEST_PRECEDENCE - 90)
@MapperScan("cn.addenda.bc.rbac.mapper")
@SpringBootApplication
public class BizRBACApplication {

    public static void main(String[] args) {
        SpringApplication.run(BizRBACApplication.class, args);
    }

}
