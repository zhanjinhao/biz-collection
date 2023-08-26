package cn.addenda.bc.seckill.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author addenda
 * @since 2022/12/7 21:19
 */
@Setter
@Getter
@ToString
public class VParamGoods {

    private String name;

    private String title;

    private String img;

    private String detail;

    private BigDecimal price;

}
