package cn.addenda.bc.rbac.mapper;

import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.bc.rbac.pojo.entity.RoleModule;
import cn.addenda.footprints.client.annotation.ConfigMasterView;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:56
 */
public interface RoleModuleMapper {

    List<RoleModule> queryModuleOfRole(@Param("roleSqc") Long roleSqc);

    void deleteBySqc(@Param("sqc") Long sqc);

    void insert(RoleModule roleModule);

    @ConfigMasterView("role")
    List<Role> queryRoleOnModule(@Param("moduleSqc") Long moduleSqc);

    void deleteByRoleSqc(@Param("roleSqc") Long roleSqc);

    Integer moduleSqcExists(@Param("moduleSqc") Long moduleSqc);

}
