package cn.addenda.bc.rbac.rpc;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.rbac.dto.DRule;
import cn.addenda.bc.rbac.pojo.entity.Rule;
import cn.addenda.bc.rbac.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/12/4 14:11
 */
@FeignClient(value = "rbac-service")
public class RuleRpcImpl implements RuleRpc {

    @Autowired
    private RuleService ruleService;

    @Override
    public List<DRule> queryRuleList(String userId) {
        AssertUtils.notNull(userId);
        List<Rule> rules = ruleService.queryRuleList(userId);
        return rules.stream().map(r -> BeanUtil.copyProperties(r, new DRule())).collect(Collectors.toList());
    }

}
