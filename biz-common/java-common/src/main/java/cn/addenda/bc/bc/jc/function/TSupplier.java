package cn.addenda.bc.bc.jc.function;

/**
 * @author addenda
 * @since 2023/6/4 14:58
 */
@FunctionalInterface
public interface TSupplier<T> {

    T get() throws Throwable;
}
