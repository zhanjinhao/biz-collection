package cn.addenda.bc.rbac.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.rbac.pojo.bo.BModuleTree;
import cn.addenda.bc.rbac.pojo.vo.VRoleWithAllFields;
import cn.addenda.bc.rbac.service.RoleModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/roleModule")
public class RoleModuleController {

    @Autowired
    private RoleModuleService roleModuleService;

    @PutMapping("/save")
    public ControllerResult<Boolean> save(@RequestParam("roleSqc") Long roleSqc, @RequestBody List<Long> moduleSqcList) {
        AssertUtils.notNull(roleSqc, "roleSqc");
        AssertUtils.notNull(moduleSqcList);

        return ControllerResult.success(roleModuleService.save(roleSqc, moduleSqcList));
    }

    @GetMapping("/queryModuleOfRole")
    public ControllerResult<BModuleTree> queryModuleOfRole(@RequestParam("roleSqc") Long roleSqc, @RequestParam("accessType") String accessType) {
        AssertUtils.notNull(roleSqc, "roleSqc");
        AssertUtils.notNull(accessType, "accessType");

        return ControllerResult.success(roleModuleService.queryModuleOfRole(roleSqc, accessType));
    }

    @GetMapping("/queryRoleOnModule")
    public ControllerResult<List<VRoleWithAllFields>> queryRoleOnModule(@RequestParam("moduleSqc") Long moduleSqc) {
        AssertUtils.notNull(moduleSqc, "moduleSqc");

        return ControllerResult.success(roleModuleService.queryRoleOnModule(moduleSqc),
                roleList -> roleList.stream()
                        .map(role -> BeanUtil.copyProperties(role, new VRoleWithAllFields()))
                        .collect(Collectors.toList()));
    }

}
