package cn.addenda.bc.rbac.service;

import cn.addenda.bc.bc.ServiceException;
import cn.addenda.bc.bc.sc.lock.LockHelper;
import cn.addenda.bc.bc.sc.lock.Locked;
import cn.addenda.bc.bc.sc.transaction.TransactionHelper;
import cn.addenda.bc.rbac.manager.UserManager;
import cn.addenda.bc.rbac.manager.UserRoleManager;
import cn.addenda.bc.rbac.pojo.entity.User;
import cn.addenda.bc.rbac.utils.StatusUtils;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserRoleManager userRoleManager;

    @Autowired
    private LockHelper lockHelper;

    @Autowired
    private TransactionHelper transactionHelper;

    @Override
    @Locked(prefix = "user:userId", spEL = "#user.userId")
    @Transactional(rollbackFor = Exception.class)
    public Long insert(User user) {
        if (userManager.userIdExists(user.getUserId())) {
            throw new ServiceException("用户ID已存在：" + user.getUserId() + "。");
        }
        return lockHelper.lock("user:userEmail", () -> {
            if (userManager.userEmailExists(user.getUserEmail())) {
                throw new ServiceException("邮箱已存在：" + user.getUserEmail() + "。");
            }
            user.setStatus(StatusUtils.ON_JOB);
            transactionHelper.doTransaction(() -> userManager.insert(user));
            return user.getSqc();
        }, user.getUserEmail());
    }

    @Override
    public PageInfo<User> pageQuery(Integer pageNum, Integer pageSize, User user) {
        try {
            PageMethod.startPage(pageNum, pageSize);
            List<User> query = userManager.queryByNonNullFields(user);
            return new PageInfo<>(query);
        } finally {
            PageMethod.clearPage();
        }
    }

    @Override
    public User queryBySqc(Long sqc) {
        return userManager.queryBySqc(sqc);
    }

    @Override
    public Boolean update(User user) {
        if (!userManager.sqcExists(user.getSqc())) {
            throw new ServiceException("sqc不存在：" + user.getSqc() + "。");
        }
        return transactionHelper.doTransaction(() -> {
            userManager.update(user);
            return true;
        });
    }

    @Override
    public Boolean setStatus(Long sqc, String status) {
        if (!StatusUtils.ON_JOB.equals(status) &&
            !StatusUtils.RETIRE.equals(status) && !StatusUtils.LEAVE.equals(status)) {
            throw new ServiceException("不合法的状态：" + status + "。");
        }
        if (!userManager.sqcExists(sqc)) {
            throw new ServiceException("sqc不存在：" + sqc + "。");
        }
        return transactionHelper.doTransaction(() -> {
            userManager.setStatus(sqc, status);
            return true;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBySqc(Long sqc) {
        userManager.deleteBySqc(sqc);
        // 删除用户的时候同步删除：用户-角色的关联
        userRoleManager.deleteByUserSqc(sqc);
        return true;
    }

    @Override
    public User queryByUserId(String userId) {
        return userManager.queryByUserId(userId);
    }

}
