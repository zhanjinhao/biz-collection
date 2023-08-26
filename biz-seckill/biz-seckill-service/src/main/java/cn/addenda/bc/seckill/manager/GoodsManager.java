package cn.addenda.bc.seckill.manager;

import cn.addenda.bc.seckill.po.Goods;

import java.util.List;

/**
 * @author addenda
 * @since 2022/12/7 21:15
 */
public interface GoodsManager {

    List<Goods> queryByNonNullFields(Goods goods);

    Goods queryByIdWithDB(Long id);

    Goods queryByIdWithPpf(Long id);

    Goods queryByIdWithRdf(Long id);

}
