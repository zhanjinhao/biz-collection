package cn.addenda.bc.rbac.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.rbac.pojo.entity.Rule;
import cn.addenda.bc.rbac.pojo.vo.VRule;
import cn.addenda.bc.rbac.pojo.vo.VUserRoleWithBizFields;
import cn.addenda.bc.rbac.service.RuleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/10/19 19:16
 */
@RestController
@RequestMapping("/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @PostMapping("/insert")
    public ControllerResult<Long> insert(@RequestBody VRule rule) {
        AssertUtils.notNull(rule);
        AssertUtils.notNull(rule.getRuleCode(), "ruleCode");
        AssertUtils.notNull(rule.getRuleName(), "ruleName");
        AssertUtils.notNull(rule.getTableName(), "tableName");
        AssertUtils.notNull(rule.getCondition(), "condition");

        return ControllerResult.success(ruleService.insert(BeanUtil.copyProperties(rule, new Rule())));
    }

    @GetMapping("/pageQuery")
    public ControllerResult<PageInfo<Rule>> pageQuery(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VRule rule) {
        AssertUtils.notNull(pageNum, "pageNum");
        AssertUtils.notNull(pageSize, "pageSize");
        AssertUtils.notNull(rule);

        return ControllerResult.success(ruleService.pageQuery(pageNum, pageSize, BeanUtil.copyProperties(rule, new Rule())));
    }

    @GetMapping("/queryBySqc")
    public ControllerResult<Rule> queryBySqc(@RequestParam("sqc") Long sqc) {
        AssertUtils.notNull(sqc, "sqc");

        return ControllerResult.success(ruleService.queryBySqc(sqc));
    }

    @PutMapping("/update")
    public ControllerResult<Boolean> update(@RequestParam("sqc") Long sqc, @RequestBody VRule rule) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(rule);
        AssertUtils.notNull(rule.getRuleName(), "ruleName");
        AssertUtils.notNull(rule.getTableName(), "tableName");
        AssertUtils.notNull(rule.getCondition(), "condition");

        AssertUtils.notModified(rule.getRuleCode(), "ruleCode");
        return ControllerResult.success(ruleService.update(BeanUtil.copyProperties(rule, new Rule(sqc))));
    }

    @PutMapping("/setStatus")
    public ControllerResult<Boolean> setStatus(@RequestParam("sqc") Long sqc, @RequestBody String status) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(status);

        return ControllerResult.success(ruleService.setStatus(sqc, status));
    }

    @DeleteMapping("/deleteBySqc")
    public ControllerResult<Boolean> deleteBySqc(@RequestBody Long sqc) {
        AssertUtils.notNull(sqc);

        return ControllerResult.success(ruleService.deleteBySqc(sqc));
    }

    @GetMapping("/queryUserRoleOnRule")
    public ControllerResult<List<VUserRoleWithBizFields>> queryUserRoleOnRule(@RequestParam("ruleSqc") Long ruleSqc) {
        AssertUtils.notNull(ruleSqc, "ruleSqc");

        return ControllerResult.success(ruleService.queryUserRoleOnRule(ruleSqc),
                urList -> urList.stream()
                        .map(ur -> BeanUtil.copyProperties(ur, new VUserRoleWithBizFields()))
                        .collect(Collectors.toList()));
    }

}
