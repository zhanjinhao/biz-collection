package cn.addenda.bc.rbac.manager;

import cn.addenda.bc.bc.jc.cache.CacheHelper;
import cn.addenda.bc.rbac.mapper.UserRoleRecordMapper;
import cn.addenda.bc.rbac.pojo.entity.UserRole;
import cn.addenda.bc.rbac.pojo.entity.UserRoleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author addenda
 * @since 2022/10/10 15:26
 */
@Component
public class UserRoleRecordManagerImpl implements UserRoleRecordManager {

    @Autowired
    private UserRoleRecordMapper userRoleRecordMapper;

    @Autowired
    private CacheHelper redisCacheHelper;

    @Override
    public UserRoleRecord queryUserRoleRecordByUserSqc(Long userSqc) {
        return userRoleRecordMapper.queryUserRoleRecordByUserSqc(userSqc);
    }

    @Override
    public void insert(UserRoleRecord userRoleRecord) {
        userRoleRecordMapper.insert(userRoleRecord);
    }

    @Override
    public void deleteByUserSqc(Long userSqc) {
        userRoleRecordMapper.deleteByUserSqc(userSqc);
    }

    @Override
    public UserRole queryLoginRole(Long userSqc) {
        return userRoleRecordMapper.queryLoginRole(userSqc);
    }

}
