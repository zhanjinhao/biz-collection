package cn.addenda.bc.bc.jc.cache.elimination;

import cn.addenda.bc.bc.jc.cache.HashMapKVCache;
import cn.addenda.bc.bc.jc.cache.KVCache;
import cn.addenda.bc.bc.jc.cache.SortedKVCache;

import java.util.LinkedHashSet;

/**
 * @author addenda
 * @since 2023/05/30
 */
public class LfuLruKVCache<K, V> extends LfuKVCache<K, V> {

    public LfuLruKVCache(long capacity) {
        super(capacity, new LruKVCache<>(capacity, new HashMapKVCache<>()));
    }

    public LfuLruKVCache(long capacity, LruKVCache<K, V> kvCacheDelegate) {
        super(capacity, kvCacheDelegate);
    }

    public LfuLruKVCache(KVCache<K, Long> keyToVisitorCount,
                         SortedKVCache<Long, LinkedHashSet<K>> visitorCountToKeySet,
                         int capacity, LruKVCache<K, V> kvCacheDelegate) {
        super(keyToVisitorCount, visitorCountToKeySet, capacity, kvCacheDelegate);
    }

}
