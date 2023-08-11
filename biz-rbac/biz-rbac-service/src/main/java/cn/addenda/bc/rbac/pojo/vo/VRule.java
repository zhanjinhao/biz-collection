package cn.addenda.bc.rbac.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/19 19:22
 */
@Setter
@Getter
@ToString
public class VRule {

    private String ruleCode;

    private String ruleName;

    private String tableName;

    private String condition;

}
