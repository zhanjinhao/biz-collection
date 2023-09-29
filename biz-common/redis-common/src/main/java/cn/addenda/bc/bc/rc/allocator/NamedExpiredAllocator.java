package cn.addenda.bc.bc.rc.allocator;

import cn.addenda.bc.bc.jc.allocator.ExpiredAllocator;

/**
 * @author addenda
 * @since 2023/9/25 23:05
 */
public interface NamedExpiredAllocator<T> extends ExpiredAllocator<T> {

    String getName();

}
