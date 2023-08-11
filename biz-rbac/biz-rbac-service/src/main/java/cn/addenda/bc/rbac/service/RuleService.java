package cn.addenda.bc.rbac.service;


import cn.addenda.bc.rbac.pojo.bo.BUserRoleWithBizFields;
import cn.addenda.bc.rbac.pojo.entity.Rule;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/19 19:18
 */
public interface RuleService {

   PageInfo<Rule> pageQuery(Integer pageNum, Integer pageSize, Rule rule);

   Rule queryBySqc(Long sqc);

   Long insert(Rule rule);

   Boolean deleteBySqc(Long sqc);

   Boolean update(Rule rule);

   Boolean setStatus(Long sqc, String status);

   List<BUserRoleWithBizFields> queryUserRoleOnRule(Long ruleSqc);

    List<Rule> queryRuleList(String userId);

}
