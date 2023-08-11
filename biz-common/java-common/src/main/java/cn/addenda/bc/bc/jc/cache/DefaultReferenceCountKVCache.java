package cn.addenda.bc.bc.jc.cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * kv-cache的包装类，用于实现引用计数。在对象有引用的时候不会被驱逐。
 *
 * @author addenda
 * @since 2023/05/30
 */
public class DefaultReferenceCountKVCache<K, V> extends KVCacheWrapper<K, V> implements ReferenceCountKVCache<K, V> {

    private KVCache<K, AtomicLong> referenceCount = new HashMapKVCache<>();

    protected DefaultReferenceCountKVCache(KVCache<K, V> kvCacheDelegate) {
        super(kvCacheDelegate);
        if (kvCacheDelegate instanceof EliminableKVCache) {
            throw new IllegalArgumentException("LruKKVCache的kvCacheDelegate不能是EliminableKVCache，当前是：" + kvCacheDelegate.getClass() + "。");
        }
    }

    public DefaultReferenceCountKVCache(KVCache<K, AtomicLong> referenceCount, KVCache<K, V> kvCacheDelegate) {
        super(kvCacheDelegate);
        this.referenceCount = referenceCount;
        if (kvCacheDelegate instanceof EliminableKVCache) {
            throw new IllegalArgumentException("LruKKVCache的kvCacheDelegate不能是EliminableKVCache，当前是：" + kvCacheDelegate.getClass() + "。");
        }
    }

    @Override
    public void set(K k, V v) {
        getKvCacheDelegate().set(k, v);
    }

    @Override
    public void set(K k, V v, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(K k) {
        return getKvCacheDelegate().containsKey(k);
    }

    @Override
    public V get(K k) {
        referenceCount.computeIfAbsent(k, s -> new AtomicLong(0L)).incrementAndGet();
        return getKvCacheDelegate().get(k);
    }

    @Override
    public void delete(K k) {
        if (referenceCount.get(k).longValue() > 0) {
            throw new CacheException("当前key被使用，不可删除！");
        }
        getKvCacheDelegate().delete(k);
    }

    @Override
    public long capacity() {
        return getKvCacheDelegate().capacity();
    }

    @Override
    public void release(K k) {
        referenceCount.computeIfAbsent(k, s -> new AtomicLong(0L)).decrementAndGet();
    }

}
