package cn.addenda.bc.rbac.rpc;

import cn.addenda.bc.rbac.dto.DUser;

/**
 * @author addenda
 * @since 2022/10/15 10:20
 */
public interface UserRpc {

    DUser queryByUserId(String userId);

}
