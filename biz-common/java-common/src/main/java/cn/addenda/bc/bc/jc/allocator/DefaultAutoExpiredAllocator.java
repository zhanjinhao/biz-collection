package cn.addenda.bc.bc.jc.allocator;

import cn.addenda.bc.bc.jc.pojo.Binary;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * @author addenda
 * @since 2023/9/13 21:35
 */
public abstract class DefaultAutoExpiredAllocator<T> implements AutoExpiredAllocator<T> {

    /**
     * key：名字
     * value: T,对象；Long,过期时间
     */
    private final Map<String, Binary<T, Long>> map = new HashMap<>();

    /**
     * key：过期时间，value：name Set
     */
    private final TreeMap<Long, Set<String>> treeMap = new TreeMap<>();

    private final Lock lock = new ReentrantLock();

    private long count = 0;

    @Setter
    private long cleaningFrequency = 100;

    @Override
    public T allocate(String name) {
        return allocate(name, TimeUnit.DAYS, 3650);
    }

    @Override
    public void release(String name) {
        lock.lock();
        try {
            Binary<T, Long> remove = map.remove(name);
            if (remove != null) {
                Long f2 = remove.getF2();
                Set<String> list = treeMap.get(f2);
                list.remove(name);
            }
            clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T allocate(String name, TimeUnit timeUnit, long timeout) {
        lock.lock();
        try {
            Param param = Param.builder().name(name).timeUnit(timeUnit).timeout(timeout).build();
            T t = referenceFunction().apply(param);
            Long expire = timeUnit.toMillis(timeout) + System.currentTimeMillis();
            map.computeIfAbsent(name, s -> new Binary<>(t, expire));
            treeMap.computeIfAbsent(expire, k -> new LinkedHashSet<>()).add(name);
            clear();
            return t;
        } finally {
            lock.unlock();
        }
    }

    private void clear() {
        count++;
        if (count % cleaningFrequency != 0) {
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Long, Set<String>>> iterator = treeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Set<String>> next = iterator.next();
            Long expire = next.getKey();
            if (expire < now) {
                iterator.remove();
            }
        }
    }

    protected abstract Function<Param, T> referenceFunction();

}
