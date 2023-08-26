package cn.addenda.bc.seckill.manager;

import cn.addenda.bc.seckill.po.SeckillOrder;

/**
 * @author addenda
 * @since 2022/12/10 17:55
 */
public interface SeckillOrderManager {

    void insert(SeckillOrder seckillOrder);

    boolean exists(Long seckillGoodsId, String userId);

    void deleteBySeckillGoodsId(Long seckillGoodsId);

}
