package cn.addenda.bc.seckill.config;

import cn.addenda.bc.bc.mc.idfilling.idgenerator.snowflake.DbWorkerIdGenerator;
import cn.addenda.bc.bc.mc.idfilling.idgenerator.snowflake.SnowflakeIdGenerator;
import cn.addenda.bc.bc.mc.idfilling.interceptor.IdFillingInterceptor;
import cn.addenda.footprints.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.footprints.client.mybatis.helper.PageHelperMsIdExtractHelper;
import cn.addenda.footprints.client.mybatis.interceptor.baseentity.MyBatisBaseEntityInterceptor;
import cn.addenda.footprints.client.mybatis.interceptor.dynamicsql.MyBatisDynamicSQLInterceptor;
import cn.addenda.footprints.client.mybatis.interceptor.lockingreads.MyBatisLockingReadsInterceptor;
import cn.addenda.footprints.client.mybatis.interceptor.sqlcheck.MyBatisSQLCheckInterceptor;
import cn.addenda.footprints.client.mybatis.interceptor.tombstone.MyBatisTombstoneInterceptor;
import cn.addenda.footprints.core.convertor.DefaultDataConvertorRegistry;
import com.github.pagehelper.PageInterceptor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @author addenda
 * @since 2023/8/7 19:01
 */
@Configuration
public class MybatisInterceptorConfig implements EnvironmentAware {

    private Environment environment;

    @Bean
    public MsIdExtractHelper msIdExtractHelper() {
        return new PageHelperMsIdExtractHelper();
    }

    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }

    @Bean
    public IdFillingInterceptor idFillingInterceptor(SnowflakeIdGenerator snowflakeIdGenerator) {
        return new IdFillingInterceptor(snowflakeIdGenerator);
    }

    @Bean
    public DbWorkerIdGenerator dbWorkerIdGenerator(DataSource dataSource) {
        return new DbWorkerIdGenerator(environment.resolvePlaceholders("${spring.application.name}"), dataSource);
    }

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(DbWorkerIdGenerator dbWorkerIdGenerator) {
        return new SnowflakeIdGenerator(dbWorkerIdGenerator);
    }

    @Bean
    public MyBatisSQLCheckInterceptor myBatisSQLCheckInterceptor(MsIdExtractHelper msIdExtractHelper) {
        return new MyBatisSQLCheckInterceptor(msIdExtractHelper);
    }

    @Bean
    public MyBatisBaseEntityInterceptor myBatisBaseEntityInterceptor(MsIdExtractHelper msIdExtractHelper) {
        return new MyBatisBaseEntityInterceptor(msIdExtractHelper);
    }

    @Bean
    public MyBatisDynamicSQLInterceptor myBatisDynamicSQLInterceptor(MsIdExtractHelper msIdExtractHelper) {
        return new MyBatisDynamicSQLInterceptor(msIdExtractHelper, new DefaultDataConvertorRegistry());
    }

    @Bean
    public MyBatisTombstoneInterceptor myBatisTombstoneInterceptor(MsIdExtractHelper msIdExtractHelper) {
        return new MyBatisTombstoneInterceptor(msIdExtractHelper);
    }

    @Bean
    public MyBatisLockingReadsInterceptor myBatisLockingReadsInterceptor(MsIdExtractHelper msIdExtractHelper) {
        return new MyBatisLockingReadsInterceptor(msIdExtractHelper);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
