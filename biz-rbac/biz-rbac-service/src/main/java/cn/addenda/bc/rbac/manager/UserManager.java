package cn.addenda.bc.rbac.manager;

import cn.addenda.bc.rbac.pojo.entity.User;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/8 17:17
 */
public interface UserManager {

    boolean userIdExists(String userId);

    boolean userEmailExists(String userId);

    boolean sqcExists(Long sqc);

    void insert(User user);

    List<User> queryByNonNullFields(User user);

    User queryBySqc(Long sqc);

    void update(User user);

    void setStatus(Long sqc, String status);

    void deleteBySqc(Long sqc);

    User queryByUserId(String userId);

}
