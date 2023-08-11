package cn.addenda.bc.rbac.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.pojo.vo.VUser;
import cn.addenda.bc.rbac.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/insert")
    public ControllerResult<Long> insert(@RequestBody VUser user) {
        AssertUtils.notNull(user);
        AssertUtils.notNull(user.getUserId(), "userId");
        AssertUtils.notNull(user.getUserEmail(), "userEmail");
        AssertUtils.notNull(user.getUserName(), "userName");

        return ControllerResult.success(userService.insert(BeanUtil.copyProperties(user, new User())));
    }

    @GetMapping("/pageQuery")
    public ControllerResult<PageInfo<User>> pageQuery(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VUser user) {
        AssertUtils.notNull(pageNum, "pageNum");
        AssertUtils.notNull(pageSize, "pageSize");
        AssertUtils.notNull(user);

        return ControllerResult.success(userService.pageQuery(pageNum, pageSize, BeanUtil.copyProperties(user, new User())));
    }

    @GetMapping("/queryBySqc")
    public ControllerResult<User> queryBySqc(@RequestParam("sqc") Long sqc) {
        AssertUtils.notNull(sqc, "sqc");

        return ControllerResult.success(userService.queryBySqc(sqc));
    }

    @PutMapping("/update")
    public ControllerResult<Boolean> update(@RequestParam("sqc") Long sqc, @RequestBody VUser user) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(user);
        AssertUtils.notNull(user.getUserName(), "userName");
        AssertUtils.notModified(user.getUserEmail(), "userEmail");
        AssertUtils.notModified(user.getUserId(), "userId");

        return ControllerResult.success(userService.update(BeanUtil.copyProperties(user, new User(sqc))));
    }

    @PutMapping("/setStatus")
    public ControllerResult<Boolean> setStatus(@RequestParam("sqc") Long sqc, @RequestBody String status) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(status);

        return ControllerResult.success(userService.setStatus(sqc, status));
    }

    @DeleteMapping("/deleteBySqc")
    public ControllerResult<Boolean> deleteBySqc(@RequestBody Long sqc) {
        AssertUtils.notNull(sqc);

        return ControllerResult.success(userService.deleteBySqc(sqc));
    }

}
