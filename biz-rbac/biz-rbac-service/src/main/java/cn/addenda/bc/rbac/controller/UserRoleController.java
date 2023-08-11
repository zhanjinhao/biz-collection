package cn.addenda.bc.rbac.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.bc.rbac.pojo.vo.VUserRolePermission;
import cn.addenda.bc.rbac.pojo.vo.VUserWithAllFields;
import cn.addenda.bc.rbac.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/8 12:56
 */
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @PutMapping("/save")
    public ControllerResult<Boolean> save(@RequestParam("userSqc") Long userSqc, @RequestBody List<Long> roleSqcList) {
        AssertUtils.notNull(userSqc, "userSqc");
        AssertUtils.notNull(roleSqcList);

        return ControllerResult.success(userRoleService.save(userSqc, roleSqcList));
    }

    @PutMapping("/setPermission")
    public ControllerResult<Boolean> setPermission(@RequestParam("sqc") Long sqc, @RequestBody VUserRolePermission rolePermission) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(rolePermission);
        AssertUtils.notNull(rolePermission.getAccessType(), "accessType");

        return ControllerResult.success(userRoleService.setPermission(sqc, BeanUtil.copyProperties(rolePermission, new UserRole())));
    }

    @GetMapping("/queryRoleOfUser")
    public ControllerResult<List<UserRole>> queryRoleOfUser(@RequestParam("userSqc") Long userSqc) {
        AssertUtils.notNull(userSqc, "userSqc");

        return ControllerResult.success(userRoleService.queryRoleOfUser(userSqc));
    }

    @GetMapping("/queryUserOnRole")
    public ControllerResult<List<VUserWithAllFields>> queryUserOnRole(@RequestParam("roleSqc") Long roleSqc) {
        AssertUtils.notNull(roleSqc, "roleSqc");

        return ControllerResult.success(userRoleService.queryUserOnRole(roleSqc),
                userList -> userList.stream()
                        .map(user -> BeanUtil.copyProperties(user, new VUserWithAllFields()))
                        .collect(Collectors.toList()));
    }

}
