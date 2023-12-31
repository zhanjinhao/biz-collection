package cn.addenda.bc.rbac.manager;


import cn.addenda.bc.rbac.pojo.entity.Role;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/9 15:41
 */
public interface RoleManager {

    boolean roleCodeExists(String roleCode);

    boolean sqcExists(Long sqc);

    void insert(Role role);

    List<Role> queryByNonNullFields(Role role);

    Role queryBySqc(Long sqc);

    void update(Role role);

    void setStatus(Long sqc, String status);

    void deleteBySqc(Long sqc);

    Role queryByRoleCode(String roleCode);

}
