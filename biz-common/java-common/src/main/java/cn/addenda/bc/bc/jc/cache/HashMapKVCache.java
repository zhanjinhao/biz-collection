package cn.addenda.bc.bc.jc.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于HashMap实现的KVCache
 *
 * @author addenda
 * @since 2023/05/30
 */
public class HashMapKVCache<K, V> implements KVCache<K, V> {

    private final Map<K, V> map = new HashMap<>();

    @Override
    public void set(K k, V v) {
        map.put(k, v);
    }

    @Override
    public void set(K k, V v, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(K k) {
        return map.containsKey(k);
    }

    @Override
    public V get(K k) {
        return map.get(k);
    }

    @Override
    public void delete(K k) {
        map.remove(k);
    }

    @Override
    public long size() {
        return map.size();
    }

    @Override
    public long capacity() {
        return Integer.MAX_VALUE;
    }

}
