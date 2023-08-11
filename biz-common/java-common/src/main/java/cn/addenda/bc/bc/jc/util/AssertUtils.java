package cn.addenda.bc.bc.jc.util;

import org.springframework.util.Assert;

/**
 * @author addenda
 * @since 2022/11/28 22:49
 */
public class AssertUtils {

    private AssertUtils() {
    }

    public static void notNull(Object condition) {
        Assert.notNull(condition, "parameter cannot be null. ");
    }

    public static void notNull(Object condition, String filedName) {
        Assert.notNull(condition, filedName + " cannot be null. ");
    }

    public static void notModified(Object condition, String filedName) {
        Assert.isNull(condition, filedName + " cannot be modified. ");
    }

    public static void notApplied(Object condition, String filedName) {
        Assert.isNull(condition, filedName + " cannot be applied. ");
    }

}
