package cn.addenda.bc.seckill.mapper;

import cn.addenda.bc.seckill.po.SeckillGoods;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeckillGoodsMapper {

    Integer datetimeConflict(@Param("goodsId") Long goodsId,
                             @Param("startDatetime") LocalDateTime startDatetime,
                             @Param("endDatetime") LocalDateTime endDatetime);

    void insert(SeckillGoods seckillGoods);

    SeckillGoods queryLatestSeckill(@Param("goodsId") Long goodsId);

    Integer existsById(@Param("id") Long id);

    Integer seckillWithPessimismLock(@Param("id") Long id);

    Long queryStock(@Param("id") Long id);

    Integer seckillWithCas(@Param("id") Long id, @Param("stock") Long stock);

    Integer seckillWithToken(@Param("id")Long id);

    void setStock(@Param("id") Long id, @Param("stock") Integer stock);

    List<SeckillGoods> queryByNonNullFields(SeckillGoods seckillGoods);

}
