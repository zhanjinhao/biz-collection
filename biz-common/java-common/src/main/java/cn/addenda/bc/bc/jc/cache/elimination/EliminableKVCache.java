package cn.addenda.bc.bc.jc.cache.elimination;

import cn.addenda.bc.bc.jc.cache.KVCache;
import cn.addenda.bc.bc.jc.cache.KVCacheWrapper;

import java.util.concurrent.TimeUnit;

/**
 * kv-cache的包装类，用于实现KVCache的淘汰策略。
 *
 * @author addenda
 * @since 2023/05/30
 */
public abstract class EliminableKVCache<K, V> extends KVCacheWrapper<K, V> {
    /**
     * cache的大小
     */
    private final long capacity;

    protected EliminableKVCache(long capacity, KVCache<K, V> kvCacheDelegate) {
        super(kvCacheDelegate);
        if (capacity <= 0L) {
            throw new IllegalArgumentException("缓存的大小需要 > 0！");
        }
        this.capacity = capacity;
    }

    @Override
    public void set(K k, V v) {
        if (containsKey(k)) {
            setWhenContainsKey(k, v);
            return;
        }
        setWhenNonContainsKey(k, v);
    }

    protected abstract void setWhenContainsKey(K k, V v);

    protected abstract void setWhenNonContainsKey(K k, V v);

    @Override
    public void set(K k, V v, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException("EliminableKVCache不支持缓存自动过期！");
//        if (containsKey(k)) {
//            setWhenContainsKey(k, v, timeout, timeunit);
//            return;
//        }
//        setWhenNonContainsKey(k, v, timeout, timeunit);
    }

    protected abstract void setWhenContainsKey(K k, V v, long timeout, TimeUnit timeunit);

    protected abstract void setWhenNonContainsKey(K k, V v, long timeout, TimeUnit timeunit);

    @Override
    public V get(K k) {
        if (!containsKey(k)) {
            return getWhenNonContainsKey(k);
        }
        return getWhenContainsKey(k);
    }

    protected abstract V getWhenNonContainsKey(K k);

    protected abstract V getWhenContainsKey(K k);

    @Override
    public boolean containsKey(K k) {
        boolean b = getKvCacheDelegate().containsKey(k);
        if (!b) {
            clearKeyUsage(k);
        }
        return b;
    }

    protected void clearKeyUsage(K k) {

    }

    @Override
    public long capacity() {
        return capacity;
    }

}
