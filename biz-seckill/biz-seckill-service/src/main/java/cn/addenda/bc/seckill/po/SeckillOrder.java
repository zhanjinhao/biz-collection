package cn.addenda.bc.seckill.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @datetime 2022/12/7 21:12
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class SeckillOrder {

    private Long id;

    private String userId;

    private Long seckillGoodsId;

    public SeckillOrder(Long id) {
        this.id = id;
    }

    public SeckillOrder(String userId, Long seckillGoodsId) {
        this.userId = userId;
        this.seckillGoodsId = seckillGoodsId;
    }
}
