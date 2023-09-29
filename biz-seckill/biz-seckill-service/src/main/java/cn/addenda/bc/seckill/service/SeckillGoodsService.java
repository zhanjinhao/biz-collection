package cn.addenda.bc.seckill.service;

import cn.addenda.bc.seckill.po.SeckillGoods;

/**
 * @author addenda
 * @since 2022/12/10 10:03
 */
public interface SeckillGoodsService {

    SeckillGoods createSeckillGoods(SeckillGoods seckillGoods);

    SeckillGoods queryLatestSeckill(Long goodsId);

    Boolean seckillWithPessimismLock(Long id);

    Boolean seckillWithPessimismLockAndOnePersonOneOrder(Long id);

    Boolean seckillWithCas(Long id);

    Boolean seckillWithCasAndOnePersonOneOrder(Long id);

    Boolean seckillWithToken(Long id);

    Boolean seckillWithTokenAndOnePersonOneOrder(Long id);

    Boolean seckillWithRedisLua(Long id);

    Boolean seckillWithTokenAndRateLimitation(Long id);

    Boolean seckillWithTokenAndRateLimitation2(Long id);

    Boolean resetSeckill(String ids, Integer stock);

}
