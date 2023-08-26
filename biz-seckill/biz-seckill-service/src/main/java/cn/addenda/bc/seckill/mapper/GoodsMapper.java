package cn.addenda.bc.seckill.mapper;

import cn.addenda.bc.seckill.po.Goods;

import java.util.List;

public interface GoodsMapper {

    List<Goods> queryByNonNullFields(Goods goods);

    List<Goods> queryOverviewByNonNullFields(Goods goods);
}
