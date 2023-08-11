package cn.addenda.bc.rbac.rpc;

import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.rbac.dto.DRole;
import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author addenda
 * @since 2022/10/15 18:01
 */
@FeignClient(value = "rbac-service")
public class RoleRpcImpl implements RoleRpc {

    @Autowired
    private RoleService roleService;

    @Override
    public DRole queryByRoleCode(String roleCode) {
        Role role = roleService.queryByRoleCode(roleCode);
        return BeanUtil.copyProperties(role, new DRole());
    }

}
