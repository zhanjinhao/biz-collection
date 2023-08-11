package cn.addenda.bc.rbac.mapper;

import cn.addenda.bc.rbac.pojo.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:55
 */
public interface UserMapper {

    Integer insert(User user);

    Integer userIdExists(@Param("userId") String userId);

    Integer userEmailExists(@Param("userEmail") String userEmail);

    Integer sqcExists(@Param("sqc") Long sqc);

    void deleteBySqc(@Param("sqc") Long sqc);

    User queryByUserId(@Param("userId") String userId);

    List<User> queryByNonNullFields(User user);

    void updateNonNullFieldsBySqc(User user);

}
