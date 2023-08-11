package cn.addenda.bc.rbac.service;

import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.pojo.entity.UserRole;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface UserRoleService {

    Boolean save(Long userSqc, List<Long> roleSqcList);

    Boolean setPermission(Long sqc, UserRole userRole);

    List<UserRole> queryRoleOfUser(Long userSqc);

    List<User> queryUserOnRole(Long roleSqc);

}
