package cn.addenda.bc.rbac.mapper;

import cn.addenda.bc.rbac.pojo.entity.Rule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleMapper {

    void insert(Rule rule);

    Integer ruleCodeExists(String ruleCode);

    Integer sqcExists(Long sqc);

    void deleteBySqc(@Param("sqc") Long sqc);

    void updateNonNullFieldsBySqc(Rule rule);

    List<Rule> queryByNonNullFields(Rule rule);

    Rule queryByRuleCode(@Param("ruleCode") String ruleCode);

}
