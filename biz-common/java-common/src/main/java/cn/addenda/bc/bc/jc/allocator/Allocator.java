package cn.addenda.bc.bc.jc.allocator;

/**
 * @author addenda
 * @since 2023/9/1 8:59
 */
public interface Allocator<T> {

    T allocate(String name);

    void release(String name);

}
