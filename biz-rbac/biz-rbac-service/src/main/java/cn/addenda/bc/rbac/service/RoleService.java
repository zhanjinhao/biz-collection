package cn.addenda.bc.rbac.service;

import cn.addenda.bc.rbac.pojo.entity.Role;
import com.github.pagehelper.PageInfo;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface RoleService {

  Long insert(Role role);

  Boolean deleteBySqc(Long sqc);

  Boolean setStatus(Long sqc, String status);

  Role queryByRoleCode(String roleCode);

  PageInfo<Role> pageQuery(Integer pageNum, Integer pageSize, Role role);

  Role queryBySqc(Long sqc);

  Boolean update(Role role);

}
