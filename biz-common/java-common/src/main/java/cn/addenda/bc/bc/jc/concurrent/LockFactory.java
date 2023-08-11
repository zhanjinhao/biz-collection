package cn.addenda.bc.bc.jc.concurrent;

import java.util.concurrent.locks.Lock;

/**
 * 在对象Factory创建的时候分配了锁，后续通过{@link LockFactory#getLock(Object)} 可以获取。
 *
 * @author addenda
 * @since 2023/6/4 16:29
 */
public interface LockFactory<K> {

    Lock getLock(K k);

}
