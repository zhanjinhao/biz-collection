package cn.addenda.bc.rbac.service;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.lock.Locked;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import cn.addenda.bc.rbac.manager.*;
import cn.addenda.bc.rbac.pojo.bo.BUserRoleWithBizFields;
import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.pojo.entity.Rule;
import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.bc.rbac.utils.StatusUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/10/19 19:18
 */
@Component
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleManager ruleManager;

    @Autowired
    private UserRoleManager userRoleManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private RoleManager roleManager;

    @Autowired
    private UserRoleRecordManager userRoleRecordManager;

    @Autowired
    private TransactionHelper transactionHelper;

    @Override
    @Locked(prefix = "rule:ruleCode", spEL = "#rule.ruleCode")
    public Long insert(Rule rule) {
        if (ruleManager.ruleCodeExists(rule.getRuleCode())) {
            throw new ServiceException("ruleCode已存在：" + rule.getRuleCode() + "。");
        }

        rule.setStatus(StatusUtils.ACTIVE);
        transactionHelper.doTransaction(() -> ruleManager.insert(rule));
        return rule.getSqc();
    }

    @Override
    public PageInfo<Rule> pageQuery(Integer pageNum, Integer pageSize, Rule rule) {
        try {
            PageMethod.startPage(pageNum, pageSize);
            List<Rule> query = ruleManager.queryByNonNullFields(rule);
            return new PageInfo<>(query);
        } finally {
            PageMethod.clearPage();
        }
    }

    @Override
    public Rule queryBySqc(Long sqc) {
        return ruleManager.queryBySqc(sqc);
    }

    @Override
    public Boolean update(Rule rule) {
        if (!ruleManager.sqcExists(rule.getSqc())) {
            throw new ServiceException("sqc不存在：" + rule.getSqc() + "。 ");
        }

        return transactionHelper.doTransaction(() -> {
            ruleManager.update(rule);
            return true;
        });
    }

    @Override
    public Boolean setStatus(Long sqc, String status) {
        StatusUtils.assertDAndAThrowSe(status);
        if (!ruleManager.sqcExists(sqc)) {
            throw new ServiceException("sqc不存在：" + sqc + "。 ");
        }

        return transactionHelper.doTransaction(() -> {
            ruleManager.setStatus(sqc, status);
            return true;
        });
    }

    @Override
    public Boolean deleteBySqc(Long sqc) {
        // 如果rule被用户关联，则不可删除
        if (userRoleManager.ruleSqcExists(sqc)) {
            throw new ServiceException("此Rule正被用户使用，不可删除！");
        }

        return transactionHelper.doTransaction(() -> {
            ruleManager.deleteBySqc(sqc);
            return true;
        });
    }

    @Override
    public List<BUserRoleWithBizFields> queryUserRoleOnRule(Long ruleSqc) {
        if (!ruleManager.sqcExists(ruleSqc)) {
            throw new ServiceException("ruleSqc不存在！");
        }
        List<UserRole> userRoleList = userRoleManager.queryUserRoleOnRule(ruleSqc);
        List<BUserRoleWithBizFields> resultList = new ArrayList<>();
        for (UserRole userRole : userRoleList) {
            BUserRoleWithBizFields result = BeanUtil.copyProperties(userRole, new BUserRoleWithBizFields());
            User user = userManager.queryBySqc(result.getUserSqc());
            BeanUtil.copyProperties(user, result);
            Role role = roleManager.queryBySqc(result.getRoleSqc());
            BeanUtil.copyProperties(role, result);
            resultList.add(result);
        }
        return resultList;
    }

    @Override
    public List<Rule> queryRuleList(String userId) {
        User user = userManager.queryByUserId(userId);
        if (user == null) {
            throw new ServiceException("用户不存在：" + userId + "。");
        }
        UserRole userRole = userRoleRecordManager.queryLoginRole(user.getSqc());
        if (userRole == null) {
            throw new ServiceException("用户未登录：" + userId + "。");
        }
        String ruleSqcListStr = userRole.getRuleSqcList();
        if (!StringUtils.hasText(ruleSqcListStr)) {
            return new ArrayList<>();
        }
        List<Long> ruleSqcList = Arrays.stream(ruleSqcListStr.split(","))
                .map(Long::valueOf).collect(Collectors.toList());
        return ruleManager.queryByRuleSqcList(ruleSqcList);
    }

}
