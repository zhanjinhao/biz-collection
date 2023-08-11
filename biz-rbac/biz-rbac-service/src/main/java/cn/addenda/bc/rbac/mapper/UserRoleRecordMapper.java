package cn.addenda.bc.rbac.mapper;

import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.bc.rbac.pojo.entity.UserRoleRecord;
import cn.addenda.footprints.client.annotation.ConfigMasterView;
import org.apache.ibatis.annotations.Param;

/**
 * @author addenda
 * @since 2022/2/7 15:56
 */
public interface UserRoleRecordMapper {

    int insert(UserRoleRecord userRoleRecord);

    UserRoleRecord queryUserRoleRecordByUserSqc(@Param("userSqc") Long userSqc);

    void deleteByUserSqc(@Param("userSqc") Long userSqc);

    @ConfigMasterView("t_user_role_record")
    UserRole queryLoginRole(@Param("userSqc") Long userSqc);

}
