package cn.addenda.bc.rbac.service;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.jc.util.IterableUtils;
import cn.addenda.bc.bc.sc.lock.LockHelper;
import cn.addenda.bc.bc.sc.transaction.TransactionAttrBuilder;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import cn.addenda.bc.bc.uc.user.UserContext;
import cn.addenda.bc.bc.uc.user.UserInfoDTO;
import cn.addenda.bc.rbac.manager.RoleManager;
import cn.addenda.bc.rbac.manager.UserManager;
import cn.addenda.bc.rbac.manager.UserRoleManager;
import cn.addenda.bc.rbac.manager.UserRoleRecordManager;
import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.bc.rbac.pojo.entity.UserRoleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class UserRoleRecordServiceImpl implements UserRoleRecordService {

    @Autowired
    private UserRoleRecordManager userRoleRecordManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private RoleManager roleManager;

    @Autowired
    private UserRoleManager userRoleManager;

    @Autowired
    private LockHelper lockHelper;

    @Autowired
    private TransactionHelper transactionHelper;

    @Override
    public UserInfoDTO login(UserRoleRecord userRoleRecord) {
        Long userSqc = userRoleRecord.getUserSqc();
        Long roleSqc = userRoleRecord.getRoleSqc();

        User user = userManager.queryBySqc(userSqc);
        if (user == null) {
            throw new ServiceException("userSqc不存在：" + userSqc + "。");
        }
        if (!roleManager.sqcExists(roleSqc)) {
            throw new ServiceException("roleSqc不存在：" + roleSqc + "。");
        }

        List<UserRole> userRoleList = IterableUtils.merge(userRoleManager.queryWRoleOfUser(userSqc), userRoleManager.queryRRoleOfUser(userSqc));
        if (!userRoleList.stream().map(UserRole::getRoleSqc).collect(Collectors.toSet()).contains(roleSqc)) {
            throw new ServiceException("用户 [" + userSqc + "] 无角色：[" + roleSqc + "]或无读写权限。");
        }

        return lockHelper.doLock("user:userSqc", () -> {

            TransactionAttribute rrAttribute = TransactionAttrBuilder.newRRBuilder().build();
            return transactionHelper.doTransaction(rrAttribute, () -> {

                // 查询出来用户现有的角色
                UserRoleRecord userRoleRecordFromDb = userRoleRecordManager.queryUserRoleRecordByUserSqc(userSqc);

                UserInfoDTO build = UserInfoDTO.builder()
                        .userId(user.getUserId())
                        .username(user.getUserName())
                        .build();

                UserContext.runWithCustomUser(() -> {
                    // 如果不存在，表示登录
                    if (userRoleRecordFromDb == null) {
                        userRoleRecord.setType(UserRoleRecord.TYPE_ENTER);
                        userRoleRecordManager.insert(userRoleRecord);
                    }
                    // 如果存在，表示转换角色
                    else {
                        userRoleRecordManager.deleteByUserSqc(userSqc);
                        userRoleRecord.setType(UserRoleRecord.TYPE_CHANGE_ROLE);
                        userRoleRecordManager.insert(userRoleRecord);
                    }

                }, build);

                return build;
            });
        }, userSqc);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean exit(Long userSqc) {

        userRoleRecordManager.deleteByUserSqc(userSqc);
        return true;
    }

    @Override
    public UserRole queryLoginRole(Long userSqc) {
        if (!userManager.sqcExists(userSqc)) {
            throw new ServiceException("userSqc不存在：" + userSqc + "。");
        }

        return userRoleRecordManager.queryLoginRole(userSqc);
    }

}
