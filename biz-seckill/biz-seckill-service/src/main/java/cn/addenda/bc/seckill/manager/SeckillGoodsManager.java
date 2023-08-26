package cn.addenda.bc.seckill.manager;

import cn.addenda.bc.seckill.po.SeckillGoods;

import java.time.LocalDateTime;

public interface SeckillGoodsManager {

    boolean datetimeConflict(Long goodsId, LocalDateTime startDatetime, LocalDateTime endDatetime);

    void insert(SeckillGoods seckillGoods);

    SeckillGoods queryLatestSeckill(Long goodsId);

    boolean existsById(Long id);

    boolean seckillWithPessimismLock(Long id);

    Long queryStock(Long id);

    boolean seckillWithCas(Long id, Long stock);

    boolean seckillWithToken(Long id);

    void setStock(Long id, Integer stock);

    SeckillGoods queryById(Long id);

}
