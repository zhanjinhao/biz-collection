package cn.addenda.bc.rbac.mapper;

import cn.addenda.bc.rbac.pojo.entity.Role;
import cn.addenda.footprints.client.annotation.DisableBaseEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:56
 */
public interface RoleMapper {

    void insert(Role role);

    @DisableBaseEntity
    Integer roleCodeExists(@Param("roleCode") String roleCode);

    @DisableBaseEntity
    Integer sqcExists(@Param("sqc") Long sqc);

    void deleteBySqc(@Param("sqc") Long sqc);

    void updateNonNullFieldsBySqc(Role role);

    Role queryByRoleCode(@Param("roleCode") String roleCode);

    List<Role> queryByNonNullFields(Role role);

}
