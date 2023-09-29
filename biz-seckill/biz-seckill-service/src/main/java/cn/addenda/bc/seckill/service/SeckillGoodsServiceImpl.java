package cn.addenda.bc.seckill.service;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.pojo.Ternary;
import cn.addenda.bc.bc.jc.util.DateUtils;
import cn.addenda.bc.bc.sc.lock.LockHelper;
import cn.addenda.bc.bc.sc.lock.Locked;
import cn.addenda.bc.bc.sc.ratelimitation.RateLimitationHelper;
import cn.addenda.bc.bc.sc.ratelimitation.RateLimited;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import cn.addenda.bc.bc.sc.util.SpELUtils;
import cn.addenda.bc.bc.uc.user.UserContext;
import cn.addenda.bc.bc.uc.user.UserInfo;
import cn.addenda.bc.seckill.manager.GoodsManager;
import cn.addenda.bc.seckill.manager.SeckillGoodsManager;
import cn.addenda.bc.seckill.manager.SeckillOrderManager;
import cn.addenda.bc.seckill.po.Goods;
import cn.addenda.bc.seckill.po.SeckillGoods;
import cn.addenda.bc.seckill.po.SeckillOrder;
import cn.addenda.footprints.core.interceptor.lockingreads.LockingReadsUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static cn.addenda.bc.seckill.constant.RedisConstant.*;

/**
 * @author addenda
 * @since 2022/12/10 10:03
 */
@Component
@Slf4j
public class SeckillGoodsServiceImpl implements SeckillGoodsService, InitializingBean {

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private SeckillGoodsManager seckillGoodsManager;

    @Autowired
    private SeckillOrderManager seckillOrderManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TransactionHelper transactionHelper;

    @Autowired
    private LockHelper lockHelper;

    @Autowired
    private RateLimitationHelper rateLimitationHelper;

    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    private static final LinkedBlockingQueue<Ternary<Long, String, UserInfo>> queue = new LinkedBlockingQueue<>();

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("redis/seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Locked(prefix = "seckillGoods", spEL = "#seckillGoods.goodsId")
    public SeckillGoods createSeckillGoods(SeckillGoods seckillGoods) {
        // 要求数据实时性，不能走缓存
        Goods goods = goodsManager.queryByIdWithDB(seckillGoods.getGoodsId());
        if (goods == null) {
            throw new ServiceException("商品不存在！");
        }

        if (seckillGoods.getStock() > goods.getStock()) {
            throw new ServiceException("秒杀商品库存最多设置为：" + goods.getStock() + "。");
        }

        LocalDateTime startDatetime = seckillGoods.getStartDatetime();
        LocalDateTime endDatetime = seckillGoods.getEndDatetime();
        if (seckillGoodsManager.datetimeConflict(seckillGoods.getGoodsId(), startDatetime, endDatetime)) {
            throw new ServiceException("[" +
                DateUtils.format(startDatetime, "yyyy-MM-dd HH:mm:ss") + ", " +
                DateUtils.format(endDatetime, "yyyy-MM-dd HH:mm:ss") + "]" +
                "时间范围内已经存在该商品的秒杀活动！");
        }

        seckillGoodsManager.insert(seckillGoods);
        stringRedisTemplate.opsForValue().set(SECKILL_GOODS_STOCK_KEY + seckillGoods.getId(), String.valueOf(seckillGoods.getStock()));
        return seckillGoods;
    }

    @Override
    public SeckillGoods queryLatestSeckill(Long goodsId) {
        return seckillGoodsManager.queryLatestSeckill(goodsId);
    }

    private void assertSeckillActive(Long id) {
        SeckillGoods seckillGoods = seckillGoodsManager.queryById(id);
        if (seckillGoods == null) {
            throw new ServiceException("秒杀商品不存在！");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDatetime = seckillGoods.getStartDatetime();
        LocalDateTime endDatetime = seckillGoods.getEndDatetime();
        // 每个秒杀时间段都是左闭右开
        if (now.isBefore(startDatetime)) {
            throw new ServiceException("秒杀未开始！");
        } else if (now.isAfter(endDatetime) || now.equals(endDatetime)) {
            throw new ServiceException("秒杀已结束！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean seckillWithPessimismLock(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        return doSeckillWithPessimismLock(id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean seckillWithPessimismLockAndOnePersonOneOrder(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        return lockHelper.lock("系统繁忙，请重试！",
            SECKILL_WITH_PESSIMISM_LOCK_AND_ONE_PERSON_ONE_ORDER_KEY,
            () -> {
                if (seckillOrderManager.exists(id, userId)) {
                    throw new ServiceException("一人只能购买一单！");
                }
                return doSeckillWithPessimismLock(id, userId);
            },
            id + ":" + userId);
    }

    private Boolean doSeckillWithPessimismLock(Long id, String userId) {
        Long stock = LockingReadsUtils.wSelect(() -> seckillGoodsManager.queryStock(id));
        if (stock <= 0) {
            return false;
        }
        if (!seckillGoodsManager.seckillWithPessimismLock(id)) {
            return false;
        }
        seckillOrderManager.insert(new SeckillOrder(userId, id));
        return true;
    }


    @Override
    public Boolean seckillWithCas(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        return doSeckillWithCas(id, userId);
    }

    @Override
    public Boolean seckillWithCasAndOnePersonOneOrder(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        return lockHelper.lock(
            "系统繁忙，请重试！",
            SECKILL_WITH_OPTIMISTIC_LOCK_AND_ONE_PERSON_ONE_ORDER_KEY,
            () -> {
                if (seckillOrderManager.exists(id, userId)) {
                    throw new ServiceException("一人只能购买一单！");
                }
                return doSeckillWithCas(id, userId);
            },
            id + ":" + userId);
    }

    private Boolean doSeckillWithCas(Long id, String userId) {
        for (int i = 0; i < 3; i++) {
            Long stock = seckillGoodsManager.queryStock(id);
            if (stock <= 0) {
                return false;
            }

            boolean tryOnce = transactionHelper.doTransaction(() -> {
                if (!seckillGoodsManager.seckillWithCas(id, stock)) {
                    return false;
                }
                seckillOrderManager.insert(new SeckillOrder(userId, id));
                return true;
            });

            if (tryOnce) {
                log.info("{} 第 {} 次尝试成功！", userId, i + 1);
                return true;
            }
        }

        log.info("{} 尝试了 {} 次，均失败！", userId, 3);
        return false;
    }

    @Override
    public Boolean seckillWithToken(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        return doSeckillWithToken(id, userId);
    }

    @Override
    public Boolean seckillWithTokenAndOnePersonOneOrder(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        return lockHelper.lock(
            "系统繁忙，请重试！",
            SECKILL_WITH_OPTIMISTIC_LOCK_AND_ONE_PERSON_ONE_ORDER_KEY,
            () -> {
                if (seckillOrderManager.exists(id, userId)) {
                    throw new ServiceException("一人只能购买一单！");
                }
                return doSeckillWithToken(id, userId);
            },
            id + ":" + userId);
    }

    private Boolean doSeckillWithToken(Long id, String userId) {
        if (!seckillGoodsManager.seckillWithToken(id)) {
            return false;
        }
        seckillOrderManager.insert(new SeckillOrder(userId, id));
        return true;
    }

    @Override
    public Boolean seckillWithRedisLua(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        Long result = stringRedisTemplate.execute(
            SECKILL_SCRIPT,
            Collections.emptyList(),
            SECKILL_GOODS_STOCK_KEY + id.toString(), userId, SECKILL_GOODS_ORDER_KEY + id.toString());
        int r = result.intValue();
        if (r == 1) {
            throw new ServiceException("一人只能购买一单！");
        } else if (r == 2) {
            return false;
        }

        // 这里应该要投MQ的
        return queue.offer(new Ternary<>(id, userId, UserContext.getUser()));
    }

    @Override
    public Boolean seckillWithTokenAndRateLimitation(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        return rateLimitationHelper.rateLimit(
            "biz-seckill:seckill", () -> doSeckillWithToken(id, userId), userId);
    }

    @Override
    @RateLimited(rateLimiterAllocator = "biz-seckill:seckill", spEL = SpELUtils.USER_ID)
    public Boolean seckillWithTokenAndRateLimitation2(Long id) {
        assertSeckillActive(id);
        String userId = UserContext.getUserId();

        return doSeckillWithToken(id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetSeckill(String ids, Integer stock) {
        for (String id : ids.split(",")) {
            seckillOrderManager.deleteBySeckillGoodsId(Long.valueOf(id));
            seckillGoodsManager.setStock(Long.valueOf(id), stock);
            stringRedisTemplate.opsForValue().set(SECKILL_GOODS_STOCK_KEY + id, String.valueOf(stock));
            stringRedisTemplate.delete(SECKILL_GOODS_ORDER_KEY + id);
        }

        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SECKILL_ORDER_EXECUTOR.submit(new SeckillOrderHandler(seckillGoodsManager, seckillOrderManager, transactionHelper));
    }

    @Slf4j
    @AllArgsConstructor
    private static class SeckillOrderHandler implements Runnable {

        private final SeckillGoodsManager seckillGoodsManager;

        private final SeckillOrderManager seckillOrderManager;

        private TransactionHelper transactionHelper;

        @Override
        public void run() {
            while (true) {
                Ternary<Long, String, UserInfo> poll;
                try {
                    poll = queue.poll(100, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error("等待阻塞队列数据时被唤醒！");
                    continue;
                }
                if (poll != null) {
                    try {
                        UserContext.runWithCustomUser(() -> {
                            transactionHelper.doTransaction(() -> {
                                seckillGoodsManager.seckillWithPessimismLock(poll.getF1());
                                seckillOrderManager.insert(new SeckillOrder(poll.getF2(), poll.getF1()));
                            });
                        }, poll.getF3());
                    } catch (Exception e) {
                        log.error("Redis扣减成功后更新数据库失败！", e);
                    }

                }
            }
        }
    }

}
