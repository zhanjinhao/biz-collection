package cn.addenda.bc.rbac.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.pojo.vo.VRole;
import cn.addenda.bc.rbac.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/insert")
    public ControllerResult<Long> insert(@RequestBody VRole role) {
        AssertUtils.notNull(role);
        AssertUtils.notNull(role.getRoleCode(), "roleCode");
        AssertUtils.notNull(role.getRoleName(), "roleName");

        return ControllerResult.success(roleService.insert(BeanUtil.copyProperties(role, new Role())));
    }

    @GetMapping("/pageQuery")
    public ControllerResult<PageInfo<Role>> pageQuery(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VRole role) {
        AssertUtils.notNull(pageNum, "pageNum");
        AssertUtils.notNull(pageSize, "pageSize");
        AssertUtils.notNull(role);

        return ControllerResult.success(roleService.pageQuery(pageNum, pageSize, BeanUtil.copyProperties(role, new Role())));
    }

    @GetMapping("/queryBySqc")
    public ControllerResult<Role> queryBySqc(@RequestParam("sqc") Long sqc) {
        AssertUtils.notNull(sqc, "sqc");

        return ControllerResult.success(roleService.queryBySqc(sqc));
    }

    @PutMapping("/update")
    public ControllerResult<Boolean> update(@RequestParam("sqc") Long sqc, @RequestBody VRole role) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(role);
        AssertUtils.notNull(role.getRoleName(), "roleName");

        AssertUtils.notModified(role.getRoleCode(), "roleCode");
        return ControllerResult.success(roleService.update(BeanUtil.copyProperties(role, new Role(sqc))));
    }

    @PutMapping("/setStatus")
    public ControllerResult<Boolean> setStatus(@RequestParam("sqc") Long sqc, @RequestBody String status) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(status);

        return ControllerResult.success(roleService.setStatus(sqc, status));
    }

    @DeleteMapping("/deleteBySqc")
    public ControllerResult<Boolean> deleteBySqc(@RequestBody Long sqc) {
        AssertUtils.notNull(sqc);

        return ControllerResult.success(roleService.deleteBySqc(sqc));
    }

}
