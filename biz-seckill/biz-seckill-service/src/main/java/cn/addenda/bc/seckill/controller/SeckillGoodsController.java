package cn.addenda.bc.seckill.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.seckill.po.SeckillGoods;
import cn.addenda.bc.seckill.service.SeckillGoodsService;
import cn.addenda.bc.seckill.vo.VParamSecKillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/12/9 19:18
 */
@RestController
@RequestMapping("/seckill")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @PostMapping("createSeckillGoods")
    public ControllerResult<SeckillGoods> createSeckillGoods(@RequestParam("goodsId") Long goodsId, @RequestBody VParamSecKillGoods secKillGoods) {
        AssertUtils.notNull(goodsId, "goodsId");
        AssertUtils.notNull(secKillGoods);
        AssertUtils.notNull(secKillGoods.getPrice(), "price");
        AssertUtils.notNull(secKillGoods.getStock(), "stock");
        AssertUtils.notNull(secKillGoods.getStartDatetime(), "startDatetime");
        AssertUtils.notNull(secKillGoods.getEndDatetime(), "endDatetime");

        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goodsId);
        return ControllerResult.success(seckillGoodsService.createSeckillGoods(BeanUtil.copyPropertiesIgnoreNull(secKillGoods, seckillGoods)));
    }

    @GetMapping("queryLatestSeckill")
    public ControllerResult<SeckillGoods> queryLatestSeckill(@RequestParam("goodsId") Long goodsId) {
        AssertUtils.notNull(goodsId, "goodsId");

        return ControllerResult.success(seckillGoodsService.queryLatestSeckill(goodsId));
    }

    @PostMapping("/seckillWithPessimismLock")
    public ControllerResult<Boolean> seckillWithPessimismLock(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(seckillGoodsService.seckillWithPessimismLock(id));
    }

    @PostMapping("/seckillWithPessimismLockAndOnePersonOneOrder")
    public ControllerResult<Boolean> seckillWithPessimismLockAndOnePersonOneOrder(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(seckillGoodsService.seckillWithPessimismLockAndOnePersonOneOrder(id));
    }

    @PostMapping("/seckillWithCas")
    public ControllerResult<Boolean> seckillWithCas(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(seckillGoodsService.seckillWithCas(id));
    }

    @PostMapping("/seckillWithCasAndOnePersonOneOrder")
    public ControllerResult<Boolean> seckillWithCasAndOnePersonOneOrder(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(seckillGoodsService.seckillWithCasAndOnePersonOneOrder(id));
    }

    @PostMapping("/seckillWithToken")
    public ControllerResult<Boolean> seckillWithToken(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(seckillGoodsService.seckillWithToken(id));
    }

    @PostMapping("/seckillWithTokenAndOnePersonOneOrder")
    public ControllerResult<Boolean> seckillWithTokenAndOnePersonOneOrder(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(seckillGoodsService.seckillWithTokenAndOnePersonOneOrder(id));
    }

    @PostMapping("/seckillWithRedisLua")
    public ControllerResult<Boolean> seckillWithRedisLua(@RequestParam("id") Long id) {
        AssertUtils.notNull(id, "id");

        return ControllerResult.success(seckillGoodsService.seckillWithRedisLua(id));
    }

    @PostMapping("/resetSeckill")
    public ControllerResult<Boolean> resetSeckill(@RequestParam("id") String ids, @RequestBody Integer stock) {
        AssertUtils.notNull(ids, "ids");
        AssertUtils.notNull(stock);

        return ControllerResult.success(seckillGoodsService.resetSeckill(ids, stock));
    }

}
