package cn.addenda.bc.rbac.manager;

import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.pojo.entity.RoleModule;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/15 15:14
 */
public interface RoleModuleManager {

    List<RoleModule> queryModuleOfRole(Long roleSqc);

    void batchDeleteBySqc(List<Long> deleteList);

    void batchInsert(List<RoleModule> insertList);

    List<Role> queryRoleOnModule(Long moduleSqc);

    void deleteByRoleSqc(Long sqc);

    boolean moduleSqcExists(Long moduleSqc);

}
