package cn.addenda.bc.rbac.service;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.pojo.Ternary;
import cn.addenda.bc.bc.jc.util.IterableUtils;
import cn.addenda.bc.bc.sc.lock.LockHelper;
import cn.addenda.bc.bc.sc.transaction.TransactionAttrBuilder;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import cn.addenda.bc.rbac.manager.RoleManager;
import cn.addenda.bc.rbac.manager.RuleManager;
import cn.addenda.bc.rbac.manager.UserManager;
import cn.addenda.bc.rbac.manager.UserRoleManager;
import cn.addenda.bc.rbac.pojo.entity.Module;
import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.pojo.entity.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Slf4j
@Component
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleManager userRoleManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private RuleManager ruleManager;

    @Autowired
    private RoleManager roleManager;

    @Autowired
    private LockHelper lockHelper;

    @Autowired
    private TransactionHelper transactionHelper;

    @Override
    public Boolean save(Long userSqc, List<Long> roleSqcList) {
        if (!userManager.sqcExists(userSqc)) {
            throw new ServiceException("userSqc不存在：" + userSqc + "。");
        }

        return lockHelper.doLock("user:userSqc", () -> {
            TransactionAttribute rrAttribute = TransactionAttrBuilder.newRRBuilder().build();
            return transactionHelper.doTransaction(rrAttribute, () -> {
                // 从数据库查出来用户已经有的角色
                List<UserRole> userRoleListFromDb = userRoleManager.queryRoleOfUser(userSqc);

                List<Long> roleSqcListFromDb = userRoleListFromDb
                        .stream()
                        .map(UserRole::getRoleSqc)
                        .collect(Collectors.toList());
                Ternary<List<Long>, List<Long>, List<Long>> separate =
                        IterableUtils.separate(roleSqcListFromDb, roleSqcList);

                // 数据库有参数没有，需要删除
                List<Long> deleteList = new ArrayList<>();
                for (Long roleSqc : separate.getF1()) {
                    Map<Long, Long> userRoleMapFromDb = userRoleListFromDb
                            .stream()
                            .collect(Collectors.toMap(UserRole::getRoleSqc, UserRole::getSqc));
                    deleteList.add(userRoleMapFromDb.get(roleSqc));
                }

                // 参数有数据库没有，需要增加
                List<UserRole> insertList = separate.getF3()
                        .stream()
                        .map(item -> new UserRole(userSqc, item, Module.AT_WRITE, ruleManager.defaultRuleSqcList()))
                        .collect(Collectors.toList());

                userRoleManager.batchDeleteBySqc(deleteList);
                userRoleManager.batchInsert(insertList);

                return true;
            });
        }, userSqc);
    }

    @Override
    public Boolean setPermission(Long sqc, UserRole userRole) {
        String accessType = userRole.getAccessType();
        if (!Module.AT_WRITE.equals(accessType) &&
                !Module.AT_READ.equals(accessType) && !Module.AT_LISTEN.equals(accessType)) {
            throw new ServiceException("不合法的进入权限：" + accessType + "。");
        }

        if (!userRoleManager.sqcExists(sqc)) {
            throw new ServiceException("sqc不存在：" + sqc + "。");
        }

        return transactionHelper.doTransaction(() -> {
            userRoleManager.setPermission(sqc, userRole);
            return true;
        });

    }

    @Override
    public List<UserRole> queryRoleOfUser(Long userSqc) {
        if (!userManager.sqcExists(userSqc)) {
            throw new ServiceException("用户ID不存在：" + userSqc + "。");
        }

        return userRoleManager.queryRoleOfUser(userSqc);
    }

    @Override
    public List<User> queryUserOnRole(Long roleSqc) {
        if (!roleManager.sqcExists(roleSqc)) {
            throw new ServiceException("roleSqc不存在：" + roleSqc + "。");
        }

        return userRoleManager.queryUserOnRole(roleSqc);
    }

}
