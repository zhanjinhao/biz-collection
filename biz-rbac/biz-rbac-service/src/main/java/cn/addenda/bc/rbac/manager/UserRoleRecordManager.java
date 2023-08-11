package cn.addenda.bc.rbac.manager;


import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.bc.rbac.pojo.entity.UserRoleRecord;

/**
 * @author addenda
 * @since 2022/10/10 15:25
 */
public interface UserRoleRecordManager {

    UserRoleRecord queryUserRoleRecordByUserSqc(Long userSqc);

    void insert(UserRoleRecord userRoleRecord);

    void deleteByUserSqc(Long userSqc);

    UserRole queryLoginRole(Long userSqc);

}
