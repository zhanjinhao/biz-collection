package cn.addenda.bc.bc.uc.baseentity;

import cn.addenda.bc.bc.uc.user.UserContext;
import cn.addenda.footprints.core.interceptor.baseentity.ThreadLocalRemarkBaseEntitySource;

/**
 * @author addenda
 * @since 2023/8/16 10:55
 */
public class UserContextBaseEntitySource extends ThreadLocalRemarkBaseEntitySource {

    @Override
    public String getCreator() {
        return UserContext.getUserId();
    }

    @Override
    public String getCreatorName() {
        return UserContext.getUsername();
    }

    @Override
    public Object getCreateTime() {
        return "now(3)";
    }

    @Override
    public String getModifier() {
        return UserContext.getUserId();
    }

    @Override
    public String getModifierName() {
        return UserContext.getUsername();
    }

    @Override
    public Object getModifyTime() {
        return "now(3)";
    }
}
