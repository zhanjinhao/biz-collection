package cn.addenda.bc.seckill.constant;

/**
 * @author addenda
 * @since 2022/12/7 22:18
 */
public class RedisConstant {

    private RedisConstant() {

    }

    public static final Long CACHE_DEFAULT_TTL = 5 * 60 * 1000L;

    public static final String GOODS_ID_KEY = "seckill:goods:id:";
    public static final String SECKILL_GOODS_ID_KEY = "seckill:seckillGoods:id:";
    public static final String SECKILL_WITH_PESSIMISM_LOCK_AND_ONE_PERSON_ONE_ORDER_KEY = "seckill:seckillGoods:plopoo:";
    public static final String SECKILL_WITH_OPTIMISTIC_LOCK_AND_ONE_PERSON_ONE_ORDER_KEY = "seckill:seckillGoods:olopoo:";

    public static final String SECKILL_GOODS_STOCK_KEY = "seckill:seckillGoods:stock:";
    public static final String SECKILL_GOODS_ORDER_KEY = "seckill:seckillGoods:order:";

}
