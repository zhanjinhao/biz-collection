package cn.addenda.bc.bc.jc.cache;

import cn.addenda.bc.bc.jc.concurrent.SimpleNamedThreadFactory;
import cn.addenda.bc.bc.jc.concurrent.allocator.LockAllocator;
import cn.addenda.bc.bc.jc.concurrent.allocator.ReentrantLockAllocator;
import cn.addenda.bc.bc.jc.trafficlimit.RequestIntervalTrafficLimiter;
import cn.addenda.bc.bc.jc.util.DateUtils;
import cn.addenda.bc.bc.jc.util.JacksonUtils;
import cn.addenda.bc.bc.jc.util.SleepUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author addenda
 * @since 2023/05/30
 */
@Slf4j
public class CacheHelper {

    public static final String NULL_OBJECT = "_NIL";

    /**
     * ppf: performance first
     */
    public static final String PERFORMANCE_FIRST_PREFIX = "pff:";
    /**
     * rdf: realtime data first
     */
    public static final String REALTIME_DATA_FIRST_PREFIX = "rdf:";
    private static final String BUILD_CACHE_SUCCESS_MSG = "构建缓存 [{}] 成功，获取到数据 [{}]，缓存到期时间 [{}]。";
    private static final String BUILD_CACHE_ERROR_MSG = "构建缓存 [{}] 失败！";

    private static final String UNEXPIRED_MSG = "获取到 [{}] 的数据 [{}] 未过期。";
    private static final String EXPIRED_MSG = "获取到 [{}] 的数据 [{}] 已过期！";
    private static final String CLEAR_MSG = "清理缓存 [{}] 成功。";

    private static final String PPF_SUBMIT_BUILD_CACHE_TASK_SUCCESS = "获取锁 [{}] 成功，提交了缓存重建任务，返回过期数据 [{}]。";
    private static final String PPF_SUBMIT_BUILD_CACHE_TASK_FAILED = "获取锁 [{}] 失败，未提交缓存重建任务，返回过期数据 [{}]。";

    private static final String RDF_TRY_LOCK_FAIL_TERMINAL = "第 [{}] 次未获取到锁 [{}]，终止获取锁";
    private static final String RDF_TRY_LOCK_FAIL_WAIT = "第 [{}] 次未获取到锁 [{}]，休眠 [{}] ms";

    /**
     * ms <p/>
     * 空 缓存多久
     */
    @Setter
    @Getter
    private Long cacheNullTtl = 5 * 60 * 1000L;

    /**
     * ms <p/>
     * ppf: 提交异步任务后等待多久 <p/>
     * rdf: 获取不到锁时休眠多久
     */
    @Setter
    @Getter
    private long lockWaitTime = 50L;

    @Setter
    @Getter
    private int rdfBusyLoop = 3;

    /**
     * key是prefix
     */
    private final Map<String, RequestIntervalTrafficLimiter> trafficLimiterMap = new ConcurrentHashMap<>();

    /**
     * 锁的管理器，防止多个线程拿到不同的锁，导致加锁失败
     */
    private final LockAllocator<? extends Lock> lockAllocator;

    /**
     * 缓存异步构建使用的线程池
     */
    private final ExecutorService cacheBuildEs;

    /**
     * 真正存储数据的缓存
     */
    private final KVCache<String, String> kvCache;

    /**
     * ppf模式下过期检测间隔（ms）
     */
    private final long ppfExpirationDetectionInterval;

    public static final long DEFAULT_PPF_EXPIRATION_DETECTION_INTERVAL = 100L;

    public CacheHelper(KVCache<String, String> kvCache, long ppfExpirationDetectionInterval,
                       LockAllocator<? extends Lock> lockAllocator, ExecutorService cacheBuildEs) {
        this.kvCache = kvCache;
        this.ppfExpirationDetectionInterval = ppfExpirationDetectionInterval;
        this.lockAllocator = lockAllocator;
        this.cacheBuildEs = cacheBuildEs;
    }

    public CacheHelper(KVCache<String, String> kvCache, long ppfExpirationDetectionInterval,
                       LockAllocator<? extends Lock> lockAllocator) {
        this.kvCache = kvCache;
        this.ppfExpirationDetectionInterval = ppfExpirationDetectionInterval;
        this.lockAllocator = lockAllocator;
        this.cacheBuildEs = defaultCacheBuildEs();
    }

    public CacheHelper(KVCache<String, String> kvCache, long ppfExpirationDetectionInterval) {
        this.kvCache = kvCache;
        this.ppfExpirationDetectionInterval = ppfExpirationDetectionInterval;
        this.lockAllocator = new ReentrantLockAllocator();
        this.cacheBuildEs = defaultCacheBuildEs();
    }

    public CacheHelper(KVCache<String, String> kvCache) {
        this.kvCache = kvCache;
        this.ppfExpirationDetectionInterval = DEFAULT_PPF_EXPIRATION_DETECTION_INTERVAL;
        this.lockAllocator = new ReentrantLockAllocator();
        this.cacheBuildEs = defaultCacheBuildEs();
    }

    protected ExecutorService defaultCacheBuildEs() {
        return new ThreadPoolExecutor(
            2,
            2,
            30,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10),
            new SimpleNamedThreadFactory("CacheHelper-Rebuild"));
    }

    public <I> void acceptWithPpf(String keyPrefix, I id, Consumer<I> consumer) {
        String key = keyPrefix + PERFORMANCE_FIRST_PREFIX + id;
        consumer.accept(id);
        kvCache.remove(key);
        log.info(CLEAR_MSG, key);
    }

    public <I> void acceptWithPpfAsync(String keyPrefix, I id, Consumer<I> consumer) {
        String key = keyPrefix + PERFORMANCE_FIRST_PREFIX + id;
        consumer.accept(id);
        cacheBuildEs.execute(() -> {
            kvCache.remove(key);
            log.info(CLEAR_MSG, key);
        });
    }

    public <I, R> R applyWithPpf(String keyPrefix, I id, Function<I, R> function) {
        String key = keyPrefix + PERFORMANCE_FIRST_PREFIX + id;
        R apply = function.apply(id);
        kvCache.remove(key);
        log.info(CLEAR_MSG, key);
        return apply;
    }

    public <I, R> R applyWithPpfAsync(String keyPrefix, I id, Function<I, R> function) {
        String key = keyPrefix + PERFORMANCE_FIRST_PREFIX + id;
        R apply = function.apply(id);
        cacheBuildEs.execute(() -> {
            kvCache.remove(key);
            log.info(CLEAR_MSG, key);
        });
        return apply;
    }

    /**
     * 性能优先的缓存查询方法，基于逻辑过期实现。
     *
     * @param keyPrefix 与id一起构成完整的键
     * @param id        键值
     * @param rType     返回值类型
     * @param rtQuery   查询实时数据
     * @param ttl       过期时间
     * @param <R>       返回值类型
     * @param <I>       键值类型
     */
    public <R, I> R queryWithPpf(
        String keyPrefix, I id, Class<R> rType, Function<I, R> rtQuery, Long ttl) {
        TypeReference<R> typeReference = new TypeReference<R>() {
            @Override
            public Type getType() {
                return rType;
            }
        };
        return queryWithPpf(keyPrefix, id, typeReference, rtQuery, ttl);
    }

    /**
     * 性能优先的缓存查询方法，基于逻辑过期实现。
     *
     * @param keyPrefix 与id一起构成完整的键
     * @param id        键值
     * @param rType     返回值类型
     * @param rtQuery   查询实时数据
     * @param ttl       过期时间
     * @param <R>       返回值类型
     * @param <I>       键值类型
     */
    public <R, I> R queryWithPpf(
        String keyPrefix, I id, TypeReference<R> rType, Function<I, R> rtQuery, Long ttl) {
        String key = keyPrefix + PERFORMANCE_FIRST_PREFIX + id;
        // 1 查询缓存
        String cachedJson = kvCache.get(key);
        // 2.1 缓存不存在则基于互斥锁构建缓存
        if (cachedJson == null) {
            // 查询数据库
            R r = queryWithRdf(keyPrefix, id, rType, rtQuery, ttl, false);
            // 存在缓存里
            setCacheData(key, r, ttl);
            return r;
        }
        // 2.2 缓存存在则进入逻辑过期的判断
        else {
            // 3.1 命中，需要先把json反序列化为对象
            CacheData<R> cacheData = (CacheData<R>) JacksonUtils.stringToObject(cachedJson, createRdTypeReference(rType));
            LocalDateTime expireTime = cacheData.getExpireTime();
            R data = cacheData.getData();
            // 4.1 判断是否过期，未过期，直接返回
            if (expireTime.isAfter(LocalDateTime.now())) {
                log.debug(UNEXPIRED_MSG, key, data);
            }
            // 4.2 判断是否过期，已过期，需要缓存重建
            else {
                // 5.1 获取互斥锁，成功，开启独立线程，进行缓存重建
                Lock lock = lockAllocator.allocateLock(getLockKey(key));
                AtomicReference<R> newData = new AtomicReference<>(null);
                if (lock.tryLock()) {
                    try {
                        cacheBuildEs.submit(() -> {
                            try {
                                // 查询数据库
                                R r = rtQuery.apply(id);
                                // 存在缓存里
                                newData.set(r);
                                setCacheData(key, r, ttl);
                            } catch (Exception e) {
                                log.error(BUILD_CACHE_ERROR_MSG, key, e);
                            }
                        });
                        log.info(PPF_SUBMIT_BUILD_CACHE_TASK_SUCCESS, getLockKey(key), data);
                        // 提交完缓存构建任务后休息一段时间，防止其他线程提交缓存构建任务
                        SleepUtils.sleep(TimeUnit.MILLISECONDS, lockWaitTime);
                        R r;
                        if ((r = newData.get()) != null) {
                            return r;
                        }
                    } finally {
                        try {
                            lock.unlock();
                        } finally {
                            lockAllocator.releaseLock(getLockKey(key));
                        }
                    }
                }
                // 5.2 获取互斥锁，未成功不进行缓存重建
                else {
                    lockAllocator.releaseLock(getLockKey(key));
                    log.info(PPF_SUBMIT_BUILD_CACHE_TASK_FAILED, getLockKey(key), data);

                    // -----------------------------------------------------------
                    // 提交重建的线程如果没有再等待时间内没有获取到新的数据，不会走下面的告警。
                    // 这是为了防止低并发下输出不必要的日志。
                    // -----------------------------------------------------------

                    // 如果过期了，输出告警信息。
                    // 使用限流器防止高并发下大量打印日志。
                    RequestIntervalTrafficLimiter trafficLimiter = trafficLimiterMap.computeIfAbsent(
                        keyPrefix + PERFORMANCE_FIRST_PREFIX, s -> new RequestIntervalTrafficLimiter(ppfExpirationDetectionInterval));
                    if (trafficLimiter.acquire()) {
                        log.error(EXPIRED_MSG, key, data);
                    }
                }
            }
            return data;
        }
    }

    private <R> void setCacheData(String key, R r, long ttl) {
        // 设置逻辑过期
        CacheData<R> newCacheData = new CacheData<>(r);
        if (r == null) {
            newCacheData.setExpireTime(LocalDateTime.now().plus(Math.min(ttl, cacheNullTtl), ChronoUnit.MILLIS));
        } else {
            newCacheData.setExpireTime(LocalDateTime.now().plus(ttl, ChronoUnit.MILLIS));
        }
        // 写缓存
        kvCache.set(key, JacksonUtils.objectToString(newCacheData), ttl * 2, TimeUnit.MILLISECONDS);
        log.info(BUILD_CACHE_SUCCESS_MSG, key, r, DateUtils.format(newCacheData.getExpireTime(), DateUtils.FULL_FORMATTER));
    }

    public <I> void acceptWithRdf(String keyPrefix, I id, Consumer<I> consumer) {
        String key = keyPrefix + REALTIME_DATA_FIRST_PREFIX + id;
        consumer.accept(id);
        kvCache.remove(key);
        log.info(CLEAR_MSG, key);
    }

    public <I> void acceptWithRdfAsync(String keyPrefix, I id, Consumer<I> consumer) {
        String key = keyPrefix + REALTIME_DATA_FIRST_PREFIX + id;
        consumer.accept(id);
        cacheBuildEs.execute(() -> {
            kvCache.remove(key);
            log.info(CLEAR_MSG, key);
        });
    }

    public <I, R> R applyWithRdf(String keyPrefix, I id, Function<I, R> function) {
        String key = keyPrefix + REALTIME_DATA_FIRST_PREFIX + id;
        R apply = function.apply(id);
        kvCache.remove(key);
        log.info(CLEAR_MSG, key);
        return apply;
    }

    public <I, R> R applyWithRdfAsync(String keyPrefix, I id, Function<I, R> function) {
        String key = keyPrefix + REALTIME_DATA_FIRST_PREFIX + id;
        R apply = function.apply(id);
        cacheBuildEs.execute(() -> {
            kvCache.remove(key);
            log.info(CLEAR_MSG, key);
        });
        return apply;
    }

    /**
     * 实时数据优先的缓存查询方法，基于互斥锁实现。
     *
     * @param keyPrefix 与id一起构成完整的键
     * @param id        键值
     * @param rType     返回值类型
     * @param rtQuery   查询实时数据
     * @param ttl       过期时间
     * @param <R>       返回值类型
     * @param <I>       键值类型
     */
    public <R, I> R queryWithRdf(
        String keyPrefix, I id, TypeReference<R> rType, Function<I, R> rtQuery, Long ttl) {
        return queryWithRdf(keyPrefix, id, rType, rtQuery, ttl, true);
    }

    /**
     * 实时数据优先的缓存查询方法，基于互持锁实现。
     *
     * @param keyPrefix 与id一起构成完整的键
     * @param id        键值
     * @param rType     返回值类型
     * @param rtQuery   查询实时数据
     * @param ttl       过期时间
     * @param <R>       返回值类型
     * @param <I>       键值类型
     */
    public <R, I> R queryWithRdf(
        String keyPrefix, I id, Class<R> rType, Function<I, R> rtQuery, Long ttl) {
        return queryWithRdf(keyPrefix, id, rType, rtQuery, ttl, true);
    }

    /**
     * 实时数据优先的缓存查询方法，基于互持锁实现。
     *
     * @param keyPrefix 与id一起构成完整的键
     * @param id        键值
     * @param rType     返回值类型
     * @param rtQuery   查询实时数据
     * @param ttl       过期时间
     * @param cache     是否将实时查询的数据缓存
     * @param <R>       返回值类型
     * @param <I>       键值类型
     */
    private <R, I> R queryWithRdf(
        String keyPrefix, I id, TypeReference<R> rType, Function<I, R> rtQuery, Long ttl, boolean cache) {
        return doQueryWithRdf(keyPrefix, id, rType, rtQuery, ttl, 0, cache);
    }

    /**
     * 实时数据优先的缓存查询方法，基于互持锁实现。
     *
     * @param keyPrefix 与id一起构成完整的键
     * @param id        键值
     * @param rType     返回值类型
     * @param rtQuery   查询实时数据
     * @param ttl       过期时间
     * @param cache     是否将实时查询的数据缓存
     * @param <R>       返回值类型
     * @param <I>       键值类型
     */
    private <R, I> R queryWithRdf(
        String keyPrefix, I id, Class<R> rType, Function<I, R> rtQuery, Long ttl, boolean cache) {
        TypeReference<R> typeReference = new TypeReference<R>() {
            @Override
            public Type getType() {
                return rType;
            }
        };
        return doQueryWithRdf(keyPrefix, id, typeReference, rtQuery, ttl, 0, cache);
    }

    /**
     * 实时数据优先的缓存查询方法，基于互持锁实现。
     *
     * @param keyPrefix 与id一起构成完整的键
     * @param id        键值
     * @param rType     返回值类型
     * @param rtQuery   查询实时数据
     * @param ttl       过期时间
     * @param itr       第几次尝试
     * @param cache     是否将实时查询的数据缓存
     * @param <R>       返回值类型
     * @param <I>       键值类型
     */
    private <R, I> R doQueryWithRdf(
        String keyPrefix, I id, TypeReference<R> rType, Function<I, R> rtQuery, Long ttl, int itr, boolean cache) {
        String key = keyPrefix + REALTIME_DATA_FIRST_PREFIX + id;
        // 1.查询缓存
        String resultJson = kvCache.get(key);
        // 2.如果返回的是占位的空值，返回null
        if (NULL_OBJECT.equals(resultJson)) {
            log.debug("获取到 [{}] 的数据为空占位。", key);
            return null;
        }
        // 3.1如果字符串不为空，返回对象
        if (resultJson != null) {
            log.debug(UNEXPIRED_MSG, key, resultJson);
            return JacksonUtils.stringToObject(resultJson, rType);
        }
        // 3.2如果字符串为空，进行缓存构建
        else {
            // todo 先过限流器，限流之后再加锁
            // 4.1获取互斥锁，获取到进行缓存构建
            Lock lock = lockAllocator.allocateLock(getLockKey(key));
            if (lock.tryLock()) {
                try {
                    R r = rtQuery.apply(id);
                    if (cache) {
                        LocalDateTime expireTime = LocalDateTime.now();
                        if (r == null) {
                            long realTtl = Math.min(cacheNullTtl, ttl);
                            expireTime = expireTime.plus(realTtl, ChronoUnit.MILLIS);
                            kvCache.set(key, NULL_OBJECT, realTtl, TimeUnit.MILLISECONDS);
                        } else {
                            expireTime = expireTime.plus(ttl, ChronoUnit.MILLIS);
                            kvCache.set(key, JacksonUtils.objectToString(r), ttl, TimeUnit.MILLISECONDS);
                        }
                        log.info(BUILD_CACHE_SUCCESS_MSG, key, r, DateUtils.format(expireTime, DateUtils.FULL_FORMATTER));
                    }
                    return r;
                } finally {
                    try {
                        lock.unlock();
                    } finally {
                        lockAllocator.releaseLock(getLockKey(key));
                    }
                }
            }
            // 4.2获取互斥锁，获取不到就休眠直至抛出异常
            else {
                lockAllocator.releaseLock(getLockKey(key));
                itr++;
                if (itr >= rdfBusyLoop) {
                    log.error(RDF_TRY_LOCK_FAIL_TERMINAL, itr, getLockKey(key));
                    throw new CacheException("系统繁忙，请稍后再试！");
                } else {
                    log.info(RDF_TRY_LOCK_FAIL_WAIT, itr, getLockKey(key), lockWaitTime);
                    SleepUtils.sleep(TimeUnit.MILLISECONDS, lockWaitTime);
                    // 递归进入的时候，当前线程的tryLock是失败的，所以当前线程不持有锁，即递归进入的状态和初次进入的状态一致
                    return doQueryWithRdf(keyPrefix, id, rType, rtQuery, ttl, itr, cache);
                }
            }
        }
    }

    private String getLockKey(String key) {
        return key + ":lock";
    }

    private <R> TypeReference<?> createRdTypeReference(TypeReference<R> typeReference) {
        TypeReference<Object> reference = new TypeReference<Object>() {
        };
        try {
            ParameterizedType a = ParameterizedTypeImpl.make(CacheData.class, new Type[]{typeReference.getType()}, null);
            Field typeField = TypeReference.class.getDeclaredField("_type");
            typeField.setAccessible(true);
            typeField.set(reference, a);
            return reference;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.error("无法设置 TypeReference 的 _type 属性！", e);
            throw new CacheException("无法设置 TypeReference 的 _type 属性！");
        }
    }

}
