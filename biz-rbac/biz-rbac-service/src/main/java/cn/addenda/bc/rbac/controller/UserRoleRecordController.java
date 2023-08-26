package cn.addenda.bc.rbac.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.bc.uc.user.UserInfo;
import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.bc.rbac.pojo.entity.UserRoleRecord;
import cn.addenda.bc.rbac.pojo.vo.VUserRoleRecord;
import cn.addenda.bc.rbac.service.UserRoleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/10/7 17:17
 */
@RestController
@RequestMapping("/userRoleRecord")
public class UserRoleRecordController {

    @Autowired
    private UserRoleRecordService userRoleRecordService;

    @PostMapping("/login")
    public ControllerResult<UserInfo> login(@RequestBody VUserRoleRecord userRoleRecord) {
        AssertUtils.notNull(userRoleRecord);
        AssertUtils.notNull(userRoleRecord.getUserSqc(), "userSqc");
        AssertUtils.notNull(userRoleRecord.getRoleSqc(), "roleSqc");

        return ControllerResult.success(userRoleRecordService.login(BeanUtil.copyProperties(userRoleRecord, new UserRoleRecord())));
    }

    @DeleteMapping("/exit")
    public ControllerResult<Boolean> exit(@RequestParam("userSqc") Long userSqc) {
        AssertUtils.notNull(userSqc, "userSqc");

        return ControllerResult.success(userRoleRecordService.exit(userSqc));
    }

    @GetMapping("/queryLoginRole")
    public ControllerResult<UserRole> queryLoginRole(@RequestParam("userSqc") Long userSqc) {
        AssertUtils.notNull(userSqc, "userSqc");

        return ControllerResult.success(userRoleRecordService.queryLoginRole(userSqc));
    }

}
