package cn.addenda.bc.rbac.rpc;

import cn.addenda.bc.rbac.dto.DRole;

/**
 * @author addenda
 * @since 2022/11/26 20:45
 */
public interface RoleRpc {

    DRole queryByRoleCode(String roleCode);

}
