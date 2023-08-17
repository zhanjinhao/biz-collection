package cn.addenda.bc.gateway.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author addenda
 * @since 2023/8/17 13:51
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKeyConstant {

    public static final String TOKENS_ALL = "gateway:tokens:all";
    public static final String TOKENS_USER_PREFIX = "gateway:tokens:user:";

}
