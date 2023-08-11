package cn.addenda.bc.bc.jc.cache;

import cn.addenda.bc.bc.jc.concurrent.LockFactory;
import cn.addenda.bc.bc.jc.concurrent.ReentrantLockFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

/**
 * kv-cache的包装类，用于实现并发安全。<p/>
 *
 * @author addenda
 * @since 2023/05/30
 */
public class ConcurrentKVCache<V> extends KVCacheWrapper<String, V> {

    private final LockFactory<String> lockFactory;

    public ConcurrentKVCache(KVCache<String, V> kvCacheDelegate) {
        this(kvCacheDelegate, new ReentrantLockFactory());
    }

    public ConcurrentKVCache(KVCache<String, V> kvCacheDelegate, LockFactory<String> lockFactory) {
        super(kvCacheDelegate);
        this.lockFactory = lockFactory;
    }

    @Override
    public void set(String k, V v) {
        Lock lock = lockFactory.getLock(k);
        lock.lock();
        try {
            getKvCacheDelegate().set(k, v);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void set(String k, V v, long timeout, TimeUnit timeunit) {
        Lock lock = lockFactory.getLock(k);

        lock.lock();
        try {
            getKvCacheDelegate().set(k, v, timeout, timeunit);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(String k) {
        Lock lock = lockFactory.getLock(k);

        lock.lock();
        try {
            return getKvCacheDelegate().containsKey(k);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(String k) {
        Lock lock = lockFactory.getLock(k);

        lock.lock();
        try {
            return getKvCacheDelegate().get(k);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String k) {
        Lock lock = lockFactory.getLock(k);

        lock.lock();
        try {
            getKvCacheDelegate().delete(k);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(String k) {
        Lock lock = lockFactory.getLock(k);

        lock.lock();
        try {
            return getKvCacheDelegate().remove(k);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
        Lock lock = lockFactory.getLock(key);

        lock.lock();
        try {
            return getKvCacheDelegate().computeIfAbsent(key, mappingFunction);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 在并发场景下，此方法的返回值可能不准
     */
    @Override
    public long capacity() {
        return getKvCacheDelegate().capacity();
    }

}
