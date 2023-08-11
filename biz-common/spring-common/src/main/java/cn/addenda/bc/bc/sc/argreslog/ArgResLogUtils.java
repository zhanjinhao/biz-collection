package cn.addenda.bc.bc.sc.argreslog;

import cn.addenda.bc.bc.SystemException;
import cn.addenda.bc.bc.jc.function.TRunnable;
import cn.addenda.bc.bc.jc.function.TSupplier;
import cn.addenda.bc.bc.jc.util.ExceptionUtil;

/**
 * @author addenda
 * @since 2023/3/9 11:17
 */
public class ArgResLogUtils extends ArgResLogSupport {

    public static void doLog(TRunnable runnable, Object... arguments) {
        doLog(null, runnable, arguments);
    }

    public static void doLog(String callerInfo, TRunnable runnable, Object... arguments) {
        doLog(callerInfo, ArgResLogAttr.builder().build(), runnable, arguments);
    }

    public static void doLog(String callerInfo, ArgResLogAttr attr, TRunnable runnable, Object... arguments) {
        doLog(callerInfo, attr, runnable.toTSupplier(), arguments);
    }

    public static <R> R doLog(TSupplier<R> supplier, Object... arguments) {
        return doLog(null, supplier, arguments);
    }

    public static <R> R doLog(String callerInfo, TSupplier<R> supplier, Object... arguments) {
        return doLog(callerInfo, ArgResLogAttr.builder().build(), supplier, arguments);
    }

    public static <R> R doLog(String callerInfo, ArgResLogAttr attr, TSupplier<R> supplier, Object... arguments) {
        try {
            return invoke(attr, arguments, supplier, callerInfo);
        } catch (Throwable throwable) {
            ExceptionUtil.reportAsRuntimeException(throwable, ArgResLogException.class);
            throw SystemException.unExpectedException();
        }
    }
}
