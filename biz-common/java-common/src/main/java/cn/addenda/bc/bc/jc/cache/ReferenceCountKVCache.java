package cn.addenda.bc.bc.jc.cache;

/**
 * @author addenda
 * @since 2023/05/30
 */
public interface ReferenceCountKVCache<K, V> extends KVCache<K, V> {

    void release(K k);

}
