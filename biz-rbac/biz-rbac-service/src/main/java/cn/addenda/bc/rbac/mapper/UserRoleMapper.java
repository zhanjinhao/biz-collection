package cn.addenda.bc.rbac.mapper;

import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.footprints.client.annotation.ConfigMasterView;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:57
 */
public interface UserRoleMapper {

    void insert(UserRole userRole);

    Integer sqcExists(@Param("sqc") Long sqc);

    void updateNonNullFieldsBySqc(UserRole userRole);

    List<UserRole> queryRoleOfUser(@Param("userSqc") Long userSqc, @Param("accessType") String accessType);

    void deleteBySqc(@Param("sqc") Long sqc);

    @ConfigMasterView("user")
    List<User> queryUserOnRole(@Param("roleSqc") Long roleSqc);

    List<UserRole> queryByNonNullFields(UserRole userRole);

    void deleteByUserSqc(@Param("userSqc") Long userSqc);

    Integer roleSqcExists(@Param("roleSqc") Long roleSqc);

    Integer ruleSqcExists(@Param("ruleSqc") Long ruleSqc);

}
