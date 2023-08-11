package cn.addenda.bc.rbac.service;

import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.bc.rbac.pojo.entity.UserRoleRecord;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface UserRoleRecordService {

    Long login(UserRoleRecord userRoleRecord);

    Boolean exit(Long userSqc);

    UserRole queryLoginRole(Long userSqc);

}
