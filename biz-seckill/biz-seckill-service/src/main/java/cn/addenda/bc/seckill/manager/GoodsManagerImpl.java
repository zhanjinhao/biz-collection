package cn.addenda.bc.seckill.manager;

import cn.addenda.bc.bc.jc.cache.CacheHelper;
import cn.addenda.bc.seckill.constant.RedisConstant;
import cn.addenda.bc.seckill.mapper.GoodsMapper;
import cn.addenda.bc.seckill.po.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/12/7 21:16
 */
@Component
public class GoodsManagerImpl implements GoodsManager {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CacheHelper cacheHelper;

    @Override
    public List<Goods> queryByNonNullFields(Goods goods) {
        return goodsMapper.queryOverviewByNonNullFields(goods);
    }

    @Override
    public Goods queryByIdWithDB(Long id) {
        List<Goods> goodsList = goodsMapper.queryByNonNullFields(new Goods(id));
        if (goodsList.isEmpty()) {
            return null;
        }
        return goodsList.get(0);
    }

    @Override
    public Goods queryByIdWithPpf(Long id) {
        return cacheHelper.queryWithPpf(RedisConstant.GOODS_ID_KEY, id, Goods.class,
            s -> queryByIdWithDB(id), RedisConstant.CACHE_DEFAULT_TTL);
    }

    @Override
    public Goods queryByIdWithRdf(Long id) {
        return cacheHelper.queryWithRdf(RedisConstant.GOODS_ID_KEY, id, Goods.class,
            s -> queryByIdWithDB(id), RedisConstant.CACHE_DEFAULT_TTL);
    }

}
