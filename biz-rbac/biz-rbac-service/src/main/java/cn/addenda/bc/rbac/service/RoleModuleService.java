package cn.addenda.bc.rbac.service;

import cn.addenda.bc.rbac.pojo.bo.BModuleTree;
import cn.addenda.bc.rbac.pojo.entity.Role;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface RoleModuleService {

    Boolean save(Long roleSqc, List<Long> moduleSqcList);

    BModuleTree queryModuleOfRole(Long roleSqc, String accessType);

    List<Role> queryRoleOnModule(Long moduleSqc);

}
