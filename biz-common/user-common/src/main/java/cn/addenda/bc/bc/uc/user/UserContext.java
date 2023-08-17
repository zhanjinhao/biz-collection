package cn.addenda.bc.bc.uc.user;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 用户上下文
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserContext {

    private static final ThreadLocal<Stack<UserInfoDTO>> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置用户至上下文
     *
     * @param user 用户详情信息
     */
    public static void setUser(UserInfoDTO user) {
        Stack<UserInfoDTO> userInfoDTOS = USER_THREAD_LOCAL.get();
        if (userInfoDTOS == null) {
            userInfoDTOS = new Stack<>();
            USER_THREAD_LOCAL.set(userInfoDTOS);
        }
        userInfoDTOS.push(user);
    }

    /**
     * 获取上下文中用户 ID
     *
     * @return 用户 ID
     */
    public static String getUserId() {
        Stack<UserInfoDTO> userInfoDTOS = USER_THREAD_LOCAL.get();
        return userInfoDTOS.peek().getUserId();
    }

    /**
     * 获取上下文中用户名称
     *
     * @return 用户名称
     */
    public static String getUsername() {
        Stack<UserInfoDTO> userInfoDTOS = USER_THREAD_LOCAL.get();
        return userInfoDTOS.peek().getUsername();
    }

    /**
     * 清理用户上下文
     */
    public static void removeUser() {
        Stack<UserInfoDTO> userInfoDTOS = USER_THREAD_LOCAL.get();
        if (userInfoDTOS == null) {
            return;
        }
        userInfoDTOS.pop();
        if (userInfoDTOS.isEmpty()) {
            USER_THREAD_LOCAL.remove();
        }
    }

    public static <T> void acceptWithCustomUser(Consumer<T> consumer, T t, UserInfoDTO userInfoDTO) {
        UserContext.setUser(userInfoDTO);
        try {
            consumer.accept(t);
        } finally {
            UserContext.removeUser();
        }
    }

    public static <T, R> R applyWithCustomUser(Function<T, R> function, T t, UserInfoDTO userInfoDTO) {
        UserContext.setUser(userInfoDTO);
        try {
            return function.apply(t);
        } finally {
            UserContext.removeUser();
        }
    }

    public static void runWithCustomUser(Runnable runnable, UserInfoDTO userInfoDTO) {
        UserContext.setUser(userInfoDTO);
        try {
            runnable.run();
        } finally {
            UserContext.removeUser();
        }
    }

    public static <R> R getWithCustomUser(Supplier<R> supplier, UserInfoDTO userInfoDTO) {
        UserContext.setUser(userInfoDTO);
        try {
            return supplier.get();
        } finally {
            UserContext.removeUser();
        }
    }

}
