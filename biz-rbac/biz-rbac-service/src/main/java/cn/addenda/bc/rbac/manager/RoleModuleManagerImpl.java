package cn.addenda.bc.rbac.manager;

import cn.addenda.bc.bc.mc.helper.MybatisBatchOperationHelper;
import cn.addenda.bc.rbac.mapper.RoleModuleMapper;
import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.pojo.entity.RoleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/15 15:14
 */
@Component
public class RoleModuleManagerImpl implements RoleModuleManager {

    @Autowired
    private RoleModuleMapper roleModuleMapper;

    @Autowired
    private MybatisBatchOperationHelper mybatisBatchOperationHelper;

    @Override
    public List<RoleModule> queryModuleOfRole(Long roleSqc) {
        return roleModuleMapper.queryModuleOfRole(roleSqc);
    }

    @Override
    public void batchDeleteBySqc(List<Long> deleteList) {
        mybatisBatchOperationHelper.batch(
                RoleModuleMapper.class, deleteList, RoleModuleMapper::deleteBySqc);
    }

    @Override
    public void batchInsert(List<RoleModule> insertList) {
        mybatisBatchOperationHelper.batch(
                RoleModuleMapper.class, insertList, RoleModuleMapper::insert);
    }

    @Override
    public List<Role> queryRoleOnModule(Long moduleSqc) {
        return roleModuleMapper.queryRoleOnModule(moduleSqc);
    }

    @Override
    public void deleteByRoleSqc(Long roleSqc) {
        roleModuleMapper.deleteByRoleSqc(roleSqc);
    }

    @Override
    public boolean moduleSqcExists(Long moduleSqc) {
        Integer integer = roleModuleMapper.moduleSqcExists(moduleSqc);
        return integer != null && integer != 0;
    }

}
