package cn.addenda.bc.seckill.po;

import cn.addenda.footprints.core.interceptor.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author addenda
 * @since 2022/12/7 20:52
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Goods extends BaseEntity {

    private Long id;

    private String name;

    private String title;

    private String img;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    public Goods(Long id) {
        this.id = id;
    }
}
