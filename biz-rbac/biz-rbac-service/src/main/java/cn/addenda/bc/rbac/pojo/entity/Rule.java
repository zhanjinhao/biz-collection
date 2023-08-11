package cn.addenda.bc.rbac.pojo.entity;

import cn.addenda.bc.bc.mc.idfilling.annotation.IdScope;
import cn.addenda.footprints.core.interceptor.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/19 19:23
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@IdScope(scopeName = "tRuleSqc", idFieldName = "sqc")
public class Rule extends BaseEntity {

    private Long sqc;

    private String ruleCode;

    private String ruleName;

    private String tableName;

    private String condition;

    private String status;

    public Rule(Long sqc) {
        this.sqc = sqc;
    }
}
