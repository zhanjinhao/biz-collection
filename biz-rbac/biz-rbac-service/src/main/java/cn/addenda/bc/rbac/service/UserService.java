package cn.addenda.bc.rbac.service;

import cn.addenda.bc.rbac.pojo.entity.User;
import com.github.pagehelper.PageInfo;

/**
 * @author addenda
 * @since 2022/2/7 17:15
 */
public interface UserService {

    Long insert(User user);

    PageInfo<User> pageQuery(Integer pageNum, Integer pageSize, User user);

    User queryBySqc(Long sqc);

    Boolean update(User user);

    Boolean setStatus(Long sqc, String status);

    Boolean deleteBySqc(Long sqc);

    User queryByUserId(String userId);

}
