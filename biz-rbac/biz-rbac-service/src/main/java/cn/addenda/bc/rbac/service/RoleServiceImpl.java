package cn.addenda.bc.rbac.service;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.sc.lock.Locked;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import cn.addenda.bc.rbac.manager.RoleManager;
import cn.addenda.bc.rbac.manager.RoleModuleManager;
import cn.addenda.bc.rbac.manager.UserRoleManager;
import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.utils.StatusUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleManager roleManager;

    @Autowired
    private UserRoleManager userRoleManager;

    @Autowired
    private RoleModuleManager roleModuleManager;

    @Autowired
    private TransactionHelper transactionHelper;

    @Override
    @Locked(prefix = "role:roleCode", spEL = "#role.roleCode")
    @Transactional(rollbackFor = Exception.class)
    public Long insert(Role role) {
        if (roleManager.roleCodeExists(role.getRoleCode())) {
            throw new ServiceException("roleCode已存在：" + role.getRoleCode() + "。 ");
        }

        role.setStatus(StatusUtils.ACTIVE);
        roleManager.insert(role);
        return role.getSqc();
    }

    @Override
    public PageInfo<Role> pageQuery(Integer pageNum, Integer pageSize, Role role) {
        try {
            PageMethod.startPage(pageNum, pageSize);
            List<Role> query = roleManager.queryByNonNullFields(role);
            return new PageInfo<>(query);
        } finally {
            PageMethod.clearPage();
        }
    }

    @Override
    public Role queryBySqc(Long sqc) {
        return roleManager.queryBySqc(sqc);
    }

    @Override
    public Boolean update(Role role) {
        if (!roleManager.sqcExists(role.getSqc())) {
            throw new ServiceException("sqc不存在：" + role.getSqc() + "。 ");
        }

        return transactionHelper.doTransaction(() -> {
            roleManager.update(role);
            return true;
        });
    }

    @Override
    public Boolean setStatus(Long sqc, String status) {
        StatusUtils.assertDAndAThrowSe(status);
        if (!roleManager.sqcExists(sqc)) {
            throw new ServiceException("sqc不存在：" + sqc + "。 ");
        }

        return transactionHelper.doTransaction(() -> {
            roleManager.setStatus(sqc, status);
            return true;
        });
    }

    @Override
    public Boolean deleteBySqc(Long sqc) {
        // 如果角色被用户关联，则不可删除
        if (userRoleManager.roleSqcExists(sqc)) {
            throw new ServiceException("此角色正被用户使用，不可删除！");
        }

        return transactionHelper.doTransaction(() -> {
            roleManager.deleteBySqc(sqc);
            // 删除角色的时候同步删除：角色-module的关联
            roleModuleManager.deleteByRoleSqc(sqc);
            return true;
        });
    }

    @Override
    public Role queryByRoleCode(String roleCode) {
        return roleManager.queryByRoleCode(roleCode);
    }

}
