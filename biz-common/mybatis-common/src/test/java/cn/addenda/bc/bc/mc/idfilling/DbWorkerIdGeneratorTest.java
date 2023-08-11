package cn.addenda.bc.bc.mc.idfilling;

import cn.addenda.bc.bc.mc.idfilling.idgenerator.snowflake.DbWorkerIdGenerator;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringValueResolver;

import javax.sql.DataSource;

/**
 * @author addenda
 * @since 2023/6/4 19:58
 */
public class DbWorkerIdGeneratorTest {

    AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext();
        context.register(TestConfiguration.class);

        context.refresh();
    }

    @Test
    public void test1() {
        DataSource dataSource = context.getBean(DataSource.class);
        DbWorkerIdGenerator dbWorkerIdGenerator = new DbWorkerIdGenerator("dbWorkerTest", dataSource);
        for (int i = 0; i < 100; i++) {
            System.out.println(dbWorkerIdGenerator.workerId());
        }
    }

    @Configuration
    @PropertySource(value = {"classpath:cn/addenda/bc/bc/mc/db.properties"})
    static class TestConfiguration implements EmbeddedValueResolverAware {

        private StringValueResolver stringValueResolver;

        @Bean
        public DataSource dataSource() {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(stringValueResolver.resolveStringValue("${db.driver}"));
            ds.setJdbcUrl(stringValueResolver.resolveStringValue("${db.url}"));
            ds.setUsername(stringValueResolver.resolveStringValue("${db.username}"));
            ds.setPassword(stringValueResolver.resolveStringValue("${db.password}"));
            ds.setMaximumPoolSize(1);

            return ds;
        }

        @Override
        public void setEmbeddedValueResolver(StringValueResolver resolver) {
            this.stringValueResolver = resolver;
        }

    }

}
