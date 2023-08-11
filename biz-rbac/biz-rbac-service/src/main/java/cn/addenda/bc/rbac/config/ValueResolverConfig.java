package cn.addenda.bc.rbac.config;

import cn.addenda.bc.bc.sc.springcontext.ValueResolverHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2022/10/23 22:15
 */
@Configuration
public class ValueResolverConfig {

    @Bean
    public ValueResolverHelper valueResolverHelper() {
        return new ValueResolverHelper();
    }

}
