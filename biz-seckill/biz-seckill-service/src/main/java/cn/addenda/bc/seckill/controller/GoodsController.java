package cn.addenda.bc.seckill.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.seckill.po.Goods;
import cn.addenda.bc.seckill.service.GoodsService;
import cn.addenda.bc.seckill.vo.VParamGoods;
import cn.addenda.bc.seckill.vo.VResultGoodsOverview;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2022/12/7 21:14
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/pageQueryOverview")
    public ControllerResult<PageInfo<VResultGoodsOverview>> pageQueryOverview(
        @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VParamGoods goods) {
        AssertUtils.notNull(pageNum, "pageNum");
        AssertUtils.notNull(pageSize, "pageSize");
        AssertUtils.notNull(goods);
        AssertUtils.notApplied(goods.getTitle(), "title");
        AssertUtils.notApplied(goods.getImg(), "img");
        AssertUtils.notApplied(goods.getDetail(), "detail");
        AssertUtils.notApplied(goods.getPrice(), "price");

        return ControllerResult.success(
            goodsService.pageQueryOverview(pageNum, pageSize, BeanUtil.copyProperties(goods, new Goods())),
            goodsPageInfo -> {
                PageInfo<VResultGoodsOverview> result = BeanUtil.copyProperties(goodsPageInfo, new PageInfo<>());
                List<VResultGoodsOverview> list = new ArrayList<>();
                for (Goods goodsItem : goodsPageInfo.getList()) {
                    list.add(BeanUtil.copyProperties(goodsItem, new VResultGoodsOverview()));
                }
                result.setList(list);
                return result;
            });
    }

    @GetMapping("/queryByIdWithDB")
    public ControllerResult<Goods> queryByIdWithDB(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(goodsService.queryByIdWithDB(id));
    }

    @GetMapping("/queryByIdWithPpf")
    public ControllerResult<Goods> queryByIdWithPpf(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(goodsService.queryByIdWithPpf(id));
    }

    @GetMapping("/queryByIdWithRdf")
    public ControllerResult<Goods> queryByIdWithRdf(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(goodsService.queryByIdWithRdf(id));
    }

}
