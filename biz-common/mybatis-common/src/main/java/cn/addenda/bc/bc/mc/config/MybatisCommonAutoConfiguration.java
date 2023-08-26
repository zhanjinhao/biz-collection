package cn.addenda.bc.bc.mc.config;

import cn.addenda.bc.bc.mc.helper.MybatisBatchOperationHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2023/8/18 15:30
 */
@AutoConfigureAfter({MybatisAutoConfiguration.class})
@Configuration
public class MybatisCommonAutoConfiguration {

    @ConditionalOnBean(value = {SqlSessionFactory.class})
    @ConditionalOnMissingBean(value = {MybatisBatchOperationHelper.class})
    @Bean
    public MybatisBatchOperationHelper mybatisBatchOperationHelper(SqlSessionFactory sqlSessionFactory) {
        return new MybatisBatchOperationHelper(sqlSessionFactory);
    }

}
