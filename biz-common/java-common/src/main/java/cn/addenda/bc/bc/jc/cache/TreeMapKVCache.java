package cn.addenda.bc.bc.jc.cache;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author addenda
 * @since 2023/05/30
 */
public class TreeMapKVCache<K, V> extends SortedMapKVCache<K, V> {

    public TreeMapKVCache() {
        super(new TreeMap<>());
    }

    public TreeMapKVCache(SortedMap<K, V> sortedMap) {
        super(sortedMap);
    }

}
