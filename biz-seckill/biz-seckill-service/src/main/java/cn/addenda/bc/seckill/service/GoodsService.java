package cn.addenda.bc.seckill.service;

import cn.addenda.bc.seckill.po.Goods;
import com.github.pagehelper.PageInfo;

public interface GoodsService {

    PageInfo<Goods> pageQueryOverview(Integer pageNum, Integer pageSize, Goods goods);

    Goods queryByIdWithDB(Long id);

    Goods queryByIdWithPpf(Long id);

    Goods queryByIdWithRdf(Long id);

}
