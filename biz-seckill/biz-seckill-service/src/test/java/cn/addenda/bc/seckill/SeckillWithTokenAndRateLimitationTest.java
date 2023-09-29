package cn.addenda.bc.seckill;

import cn.addenda.bc.bc.uc.user.UserContext;
import cn.addenda.bc.bc.uc.user.UserInfo;
import cn.addenda.bc.seckill.service.SeckillGoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author addenda
 * @since 2023/9/28 21:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BizSeckillApplication.class})
public class SeckillWithTokenAndRateLimitationTest {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Test
    public void test1() {

        UserContext.runWithCustomUser(new Runnable() {
            @Override
            public void run() {
                seckillGoodsService.resetSeckill("5", 1000);
                seckillGoodsService.seckillWithTokenAndRateLimitation(5L);
            }
        }, UserInfo.builder().userId("rate").build());

    }

    @Test
    public void test2() {

        UserContext.runWithCustomUser(new Runnable() {
            @Override
            public void run() {
                seckillGoodsService.resetSeckill("5", 1000);
                seckillGoodsService.seckillWithTokenAndRateLimitation2(5L);
            }
        }, UserInfo.builder().userId("rate").build());

    }

}
