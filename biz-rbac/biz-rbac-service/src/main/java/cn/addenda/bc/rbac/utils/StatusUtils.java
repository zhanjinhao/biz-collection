package cn.addenda.bc.rbac.utils;

import cn.addenda.bc.bc.ServiceException;

/**
 * @author addenda
 * @since 2022/10/19 19:31
 */
public class StatusUtils {

    private StatusUtils() {
    }

    public static final String DISABLE = "D";

    public static final String ACTIVE = "A";

    public static final String ON_JOB = "J";

    public static final String RETIRE = "R";

    public static final String LEAVE = "L";

    public static void assertDAndAThrowSe(String status) {
        if (!ACTIVE.equals(status) && !DISABLE.equals(status)) {
            throw new ServiceException("不合法的状态：" + status + "。");
        }
    }

    public static void assertDAndAThrowIae(String status) {
        if (!ACTIVE.equals(status) && !DISABLE.equals(status)) {
            throw new IllegalArgumentException("不合法的状态：" + status + "。");
        }
    }

}
