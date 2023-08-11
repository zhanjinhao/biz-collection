package cn.addenda.bc.rbac.config;

import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2022/10/23 20:12
 */
@Configuration
public class TransactionHelperConfig {

    @Bean
    public TransactionHelper transactionHelper() {
        return new TransactionHelper();
    }

}
