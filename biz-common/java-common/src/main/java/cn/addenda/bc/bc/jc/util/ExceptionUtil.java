/*
 * copy from mybatis project.
 */
package cn.addenda.bc.bc.jc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author Clinton Begin
 */
public class ExceptionUtil {

    private ExceptionUtil() {
        // Prevent Instantiation
    }

    public static Throwable unwrapThrowable(Throwable wrapped) {
        if (wrapped == null) {
            return null;
        }
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    public static void reportAsRuntimeException(Throwable throwable, Class<? extends RuntimeException> exception) {
        throwable = ExceptionUtil.unwrapThrowable(throwable);
        if (throwable != null && !(exception.isAssignableFrom(throwable.getClass()))) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new UndeclaredThrowableException(throwable);
            }
        }

        throw exception.cast(throwable);
    }

}
