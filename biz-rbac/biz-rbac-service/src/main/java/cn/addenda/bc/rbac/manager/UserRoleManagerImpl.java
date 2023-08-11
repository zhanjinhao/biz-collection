package cn.addenda.bc.rbac.manager;

import cn.addenda.bc.bc.mc.helper.MybatisBatchOperationHelper;
import cn.addenda.bc.rbac.mapper.UserRoleMapper;
import cn.addenda.bc.rbac.pojo.entity.Module;
import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.pojo.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/9 10:22
 */
@Component
public class UserRoleManagerImpl implements UserRoleManager {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private MybatisBatchOperationHelper mybatisBatchOperationHelper;

    @Override
    public void deleteByUserSqc(Long userSqc) {
        userRoleMapper.deleteByUserSqc(userSqc);
    }

    @Override
    public void batchDeleteBySqc(List<Long> deleteList) {
        mybatisBatchOperationHelper.batch(
                UserRoleMapper.class, deleteList, UserRoleMapper::deleteBySqc);
    }

    @Override
    public void batchInsert(List<UserRole> insertList) {
        mybatisBatchOperationHelper.batch(
                UserRoleMapper.class, insertList, UserRoleMapper::insert);
    }

    @Override
    public boolean sqcExists(Long sqc) {
        Integer integer = userRoleMapper.sqcExists(sqc);
        return integer != null && integer > 0;
    }

    @Override
    public void setPermission(Long sqc, UserRole userRole) {
        userRole.setSqc(sqc);
        userRoleMapper.updateNonNullFieldsBySqc(userRole);
    }

    @Override
    public List<UserRole> queryRoleOfUser(Long userSqc) {
        return userRoleMapper.queryRoleOfUser(userSqc, null);
    }

    @Override
    public List<UserRole> queryWRoleOfUser(Long userSqc) {
        return userRoleMapper.queryRoleOfUser(userSqc, Module.AT_WRITE);
    }

    @Override
    public List<UserRole> queryRRoleOfUser(Long userSqc) {
        return userRoleMapper.queryRoleOfUser(userSqc, Module.AT_READ);
    }

    @Override
    public List<User> queryUserOnRole(Long roleSqc) {
        return userRoleMapper.queryUserOnRole(roleSqc);
    }

    @Override
    public List<UserRole> queryUserRoleOnRule(Long ruleSqc) {
        UserRole userRole = new UserRole();
        userRole.setRuleSqcList(String.valueOf(ruleSqc));
        return userRoleMapper.queryByNonNullFields(userRole);
    }

    @Override
    public boolean roleSqcExists(Long roleSqc) {
        Integer integer = userRoleMapper.roleSqcExists(roleSqc);
        return integer != null && integer != 0;
    }

    @Override
    public boolean ruleSqcExists(Long ruleSqc) {
        Integer integer = userRoleMapper.ruleSqcExists(ruleSqc);
        return integer != null && integer != 0;
    }
}
