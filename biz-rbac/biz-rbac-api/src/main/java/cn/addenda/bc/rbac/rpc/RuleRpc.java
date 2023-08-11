package cn.addenda.bc.rbac.rpc;


import cn.addenda.bc.rbac.dto.DRule;

import java.util.List;

/**
 * @author addenda
 * @since 2022/11/26 20:45
 */
public interface RuleRpc {

    List<DRule> queryRuleList(String userId);

}
