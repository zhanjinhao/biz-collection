package cn.addenda.bc.seckill.manager;

import cn.addenda.bc.seckill.mapper.SeckillOrderMapper;
import cn.addenda.bc.seckill.po.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author addenda
 * @since 2022/12/10 17:55
 */
@Component
public class SeckillOrderManagerImpl implements SeckillOrderManager {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public void insert(SeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }

    @Override
    public boolean exists(Long seckillGoodsId, String userId) {
        Integer count = seckillOrderMapper.exists(seckillGoodsId, userId);
        return count != null && count > 0;
    }

    @Override
    public void deleteBySeckillGoodsId(Long seckillGoodsId) {
        seckillOrderMapper.deleteBySeckillGoodsId(seckillGoodsId);
    }
}
