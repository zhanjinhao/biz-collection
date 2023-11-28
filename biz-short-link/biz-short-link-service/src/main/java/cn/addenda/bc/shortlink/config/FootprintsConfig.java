package cn.addenda.bc.shortlink.config;

import cn.addenda.bc.bc.uc.baseentity.UserContextBaseEntitySource;
import cn.addenda.footprints.client.spring.aop.baseentity.BaseEntityRewriterConfigurer;
import cn.addenda.footprints.client.spring.aop.baseentity.EnableBaseEntity;
import cn.addenda.footprints.client.spring.aop.dynamicsql.DataConvertorRegistryConfigurer;
import cn.addenda.footprints.client.spring.aop.dynamicsql.DynamicSQLRewriterConfigurer;
import cn.addenda.footprints.client.spring.aop.dynamicsql.EnableDynamicSQL;
import cn.addenda.footprints.client.spring.aop.lockingreads.EnableLockingReads;
import cn.addenda.footprints.client.spring.aop.sqlcheck.EnableSQLCheck;
import cn.addenda.footprints.client.spring.aop.sqlcheck.SQLCheckConfigurer;
import cn.addenda.footprints.client.spring.aop.tombstone.EnableTombstone;
import cn.addenda.footprints.client.spring.aop.tombstone.TombstoneRewriterConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2023/8/7 19:16
 */
@EnableLockingReads(order = 5)
@EnableTombstone(order = 4)
@EnableBaseEntity(order = 3)
@EnableDynamicSQL(order = 2)
@EnableSQLCheck(order = 1)
@Configuration
public class FootprintsConfig {

    @Bean
    public TombstoneRewriterConfigurer tombstoneRewriterConfigurer() {
        return new TombstoneRewriterConfigurer();
    }

    @Bean
    public DataConvertorRegistryConfigurer dataConvertorRegistryConfigurer() {
        return new DataConvertorRegistryConfigurer();
    }

    @Bean
    public DynamicSQLRewriterConfigurer dynamicSQLRewriterConfigurer() {
        return new DynamicSQLRewriterConfigurer();
    }

    @Bean
    public BaseEntityRewriterConfigurer baseEntityRewriterConfigurer() {
        return new BaseEntityRewriterConfigurer(new UserContextBaseEntitySource());
    }

    @Bean
    public SQLCheckConfigurer sqlCheckConfigurer() {
        return new SQLCheckConfigurer();
    }

}
