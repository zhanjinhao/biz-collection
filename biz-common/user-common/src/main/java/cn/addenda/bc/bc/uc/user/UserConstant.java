package cn.addenda.bc.bc.uc.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 用户常量
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserConstant {

    /**
     * 用户 ID Key
     */
    public static final String USER_ID_KEY = "userId";

    /**
     * 用户名 Key
     */
    public static final String USER_NAME_KEY = "username";

    /**
     * 用户 Token Key
     */
    public static final String AUTHORIZATION = "Authorization";
}
