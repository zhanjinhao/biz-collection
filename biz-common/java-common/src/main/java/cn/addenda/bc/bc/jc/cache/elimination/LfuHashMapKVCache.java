package cn.addenda.bc.bc.jc.cache.elimination;

import cn.addenda.bc.bc.jc.cache.HashMapKVCache;
import cn.addenda.bc.bc.jc.cache.KVCache;
import cn.addenda.bc.bc.jc.cache.SortedKVCache;

import java.util.LinkedHashSet;

/**
 * @author addenda
 * @since 2023/05/30
 */
public class LfuHashMapKVCache<K, V> extends LfuKVCache<K, V> {

    public LfuHashMapKVCache(long capacity) {
        super(capacity, new HashMapKVCache<>());
    }

    public LfuHashMapKVCache(KVCache<K, Long> keyToVisitorCount, SortedKVCache<Long, LinkedHashSet<K>> visitorCountToKeySet, long capacity) {
        super(keyToVisitorCount, visitorCountToKeySet, capacity, new HashMapKVCache<>());
    }



}
