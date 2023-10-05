package cn.addenda.bc.bc.imdepotence;

import cn.addenda.bc.bc.sc.idempotence.ConsumeMode;
import cn.addenda.bc.bc.sc.idempotence.ConsumeStatus;
import cn.addenda.bc.bc.sc.idempotence.DbStorageCenter;
import cn.addenda.bc.bc.sc.idempotence.IdempotenceParamWrapper;
import cn.addenda.bc.seckill.BizSeckillApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author addenda
 * @since 2023/9/17 12:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BizSeckillApplication.class})
@Slf4j
public class DbStorageCenterTest {

    @Autowired
    private DbStorageCenter dbStorageCenter;

    @Test
    public void test1() {
        IdempotenceParamWrapper build = IdempotenceParamWrapper.builder()
            .key("DbStorageCenterTest")
            .namespace("spring-common")
            .prefix("addenda")
            .consumeMode(ConsumeMode.SUCCESS)
            .xId("1")
            .ttlSecs(600).build();
        ConsumeStatus set = dbStorageCenter.getSet(build, ConsumeStatus.CONSUMING);
        Assert.assertEquals(set, null);
    }

    @Test
    public void test2() {
        IdempotenceParamWrapper build = IdempotenceParamWrapper.builder()
            .key("DbStorageCenterTest")
            .namespace("spring-common")
            .prefix("addenda")
            .consumeMode(ConsumeMode.SUCCESS)
            .xId("1")
            .ttlSecs(600).build();
        ConsumeStatus set = dbStorageCenter.getSet(build, ConsumeStatus.CONSUMING);
        Assert.assertEquals(set, ConsumeStatus.CONSUMING);
    }

    @Test
    public void test3() {
        IdempotenceParamWrapper build = IdempotenceParamWrapper.builder()
            .key("DbStorageCenterTest")
            .namespace("spring-common")
            .prefix("addenda")
            .xId("1")
            .consumeMode(ConsumeMode.SUCCESS)
            .ttlSecs(600).build();
        boolean b = dbStorageCenter.casStatus(build, ConsumeStatus.CONSUMING, ConsumeStatus.SUCCESS, false);
        log.info("casStatus: {}", b);
        Assert.assertEquals(b, true);
    }

    @Test
    public void test4() {
        IdempotenceParamWrapper build = IdempotenceParamWrapper.builder()
            .key("DbStorageCenterTest")
            .namespace("spring-common")
            .prefix("addenda")
            .xId("1")
            .consumeMode(ConsumeMode.SUCCESS)
            .ttlSecs(600).build();
        boolean b = dbStorageCenter.casStatus(build, ConsumeStatus.CONSUMING, ConsumeStatus.SUCCESS, false);
        log.info("casStatus: {}", b);
        Assert.assertEquals(b, false);
    }

    @Test
    public void test5() {
        IdempotenceParamWrapper build = IdempotenceParamWrapper.builder()
            .key("DbStorageCenterTest")
            .namespace("spring-common")
            .prefix("addenda")
            .xId("2")
            .consumeMode(ConsumeMode.SUCCESS)
            .ttlSecs(600).build();
        boolean b = dbStorageCenter.casStatus(build, ConsumeStatus.SUCCESS, ConsumeStatus.CONSUMING, false);
        log.info("casStatus: {}", b);
        Assert.assertEquals(b, false);
    }

    @Test
    public void test6() {
        IdempotenceParamWrapper build = IdempotenceParamWrapper.builder()
            .key("DbStorageCenterTest")
            .namespace("spring-common")
            .prefix("addenda")
            .xId("2")
            .consumeMode(ConsumeMode.SUCCESS)
            .ttlSecs(600).build();
        boolean b = dbStorageCenter.casStatus(build, ConsumeStatus.SUCCESS, ConsumeStatus.CONSUMING, true);
        log.info("casStatus: {}", b);
        Assert.assertEquals(b, true);
    }

    @Test
    public void test7() {
        IdempotenceParamWrapper build = IdempotenceParamWrapper.builder()
            .key("DbStorageCenterTest")
            .namespace("spring-common")
            .prefix("addenda")
            .xId("1")
            .consumeMode(ConsumeMode.SUCCESS)
            .ttlSecs(600).build();
        boolean delete = dbStorageCenter.delete(build);
        Assert.assertEquals(delete, false);
    }

    @Test
    public void test8() {
        IdempotenceParamWrapper build = IdempotenceParamWrapper.builder()
            .key("DbStorageCenterTest")
            .namespace("spring-common")
            .prefix("addenda")
            .xId("2")
            .consumeMode(ConsumeMode.SUCCESS)
            .ttlSecs(600).build();
        boolean delete = dbStorageCenter.delete(build);
        Assert.assertEquals(delete, true);
    }


    @Test
    public void test9() {
        dbStorageCenter.clearExpiredKey();
    }


}
