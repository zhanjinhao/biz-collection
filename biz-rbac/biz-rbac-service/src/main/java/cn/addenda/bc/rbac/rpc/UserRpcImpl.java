package cn.addenda.bc.rbac.rpc;

import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.rbac.dto.DUser;
import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author addenda
 * @since 2022/10/15 10:21
 */
@FeignClient(value = "rbac-service")
public class UserRpcImpl implements UserRpc {

    @Autowired
    private UserService userService;

    @Override
    public DUser queryByUserId(String userId) {
        User user = userService.queryByUserId(userId);
        return BeanUtil.copyProperties(user, new DUser());
    }

}
