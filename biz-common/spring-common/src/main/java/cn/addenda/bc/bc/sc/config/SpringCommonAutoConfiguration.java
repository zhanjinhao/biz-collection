package cn.addenda.bc.bc.sc.config;

import cn.addenda.bc.bc.sc.springcontext.ValueResolverHelper;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2023/8/18 15:22
 */
@Configuration
public class SpringCommonAutoConfiguration {

    @ConditionalOnMissingBean(value = TransactionHelper.class)
    @Bean
    public TransactionHelper transactionHelper() {
        return new TransactionHelper();
    }

    @ConditionalOnMissingBean(value = ValueResolverHelper.class)
    @Bean
    public ValueResolverHelper valueResolverHelper() {
        return new ValueResolverHelper();
    }

}
