package cn.addenda.bc.rbac.config;

import cn.addenda.bc.bc.mc.helper.MybatisBatchOperationHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2022/10/27 19:31
 */
@Configuration
public class MybatisBatchOperationConfig {

    @Bean
    public MybatisBatchOperationHelper mybatisBatchOperationHelper(SqlSessionFactory sqlSessionFactory) {
        return new MybatisBatchOperationHelper(sqlSessionFactory);
    }

}
