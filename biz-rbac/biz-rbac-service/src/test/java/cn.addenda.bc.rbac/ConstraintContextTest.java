package cn.addenda.bc.rbac;

import cn.addenda.bc.rbac.dto.DRule;
import cn.addenda.bc.rbac.manager.RoleModuleManager;
import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.rpc.RuleRpc;
import cn.addenda.footprints.core.interceptor.dynamicsql.DynamicSQLUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


/**
 * @author addenda
 * @since 2022/12/4 15:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConstraintContextTest {

    @Autowired
    private RuleRpc ruleRpc;

    @Autowired
    private RoleModuleManager roleModuleManager;

    @Test
    public void test1() {
        List<DRule> dRules = ruleRpc.queryRuleList("126");
        dRules.forEach(System.out::println);
        System.out.println("------------------------");
        for (DRule dRule : dRules) {
            DynamicSQLUtils.tableAddJoinCondition(dRule.getTableName(), dRule.getCondition(), () -> {
                List<Role> roles = roleModuleManager.queryRoleOnModule(10000L);
                roles.forEach(System.out::println);
            });
        }
    }

}
