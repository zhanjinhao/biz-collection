package cn.addenda.bc.seckill.service;

import cn.addenda.bc.seckill.manager.GoodsManager;
import cn.addenda.bc.seckill.po.Goods;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/12/7 21:16
 */
@Component
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsManager goodsManager;

    @Override
    public PageInfo<Goods> pageQueryOverview(Integer pageNum, Integer pageSize, Goods goods) {
        try {
            PageMethod.startPage(pageNum, pageSize);
            List<Goods> query = goodsManager.queryByNonNullFields(goods);
            return new PageInfo<>(query);
        } finally {
            PageMethod.clearPage();
        }
    }

    @Override
    public Goods queryByIdWithDB(Long id) {
        return goodsManager.queryByIdWithDB(id);
    }

    @Override
    public Goods queryByIdWithPpf(Long id) {
        return goodsManager.queryByIdWithPpf(id);
    }

    @Override
    public Goods queryByIdWithRdf(Long id) {
        return goodsManager.queryByIdWithRdf(id);
    }

}
