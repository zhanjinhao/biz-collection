package cn.addenda.bc.bc.jc.util;

import cn.addenda.bc.bc.jc.function.BiConsumerBiFunction;
import cn.addenda.bc.bc.jc.function.ConsumerFunction;
import cn.addenda.bc.bc.jc.function.FunctionConverter;
import cn.addenda.bc.bc.jc.pojo.Ternary;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author addenda
 * @since 2023/6/5 21:27
 */
@Slf4j
public class IterableUtils {

    private IterableUtils() {
    }

    private static final int BATCH_SIZE = 100;

    public static <T> Ternary<List<T>, List<T>, List<T>> separate(Iterable<T> a, Iterable<T> b) {
        if (a == null && b == null) {
            return new Ternary<>();
        }
        if (a == null) {
            return new Ternary<>(new ArrayList<>(), new ArrayList<>(), toList(b));
        }
        if (b == null) {
            return new Ternary<>(new ArrayList<>(), new ArrayList<>(), toList(a));
        }
        a = toList(a);
        b = toList(b);
        List<T> inAButNotInB = new ArrayList<>();
        List<T> inAAndB = new ArrayList<>();

        for (T t : a) {
            Iterator<T> iterator = b.iterator();
            boolean fg = false;
            while (iterator.hasNext()) {
                T next = iterator.next();
                if (t.equals(next)) {
                    inAAndB.add(t);
                    iterator.remove();
                    fg = true;
                    break;
                }
            }
            if (!fg) {
                inAButNotInB.add(t);
            }
        }

        List<T> notInAButInB = toList(b);

        return new Ternary<>(inAButNotInB, inAAndB, notInAButInB);
    }

    /**
     * 集合做拆分
     */
    public static <T> List<List<T>> split(Iterable<T> iterable, int quantity) {
        if (iterable == null) {
            return new ArrayList<>();
        }
        List<List<T>> listList = new ArrayList<>();
        List<T> seg = null;
        int i = 0;
        for (T t : iterable) {
            if (i % quantity == 0) {
                if (seg != null) {
                    listList.add(seg);
                }
                seg = new ArrayList<>();
            }
            seg.add(t);
            i++;
        }
        if (seg != null && !seg.isEmpty()) {
            listList.add(seg);
        }
        return listList;
    }

    public static <T> void acceptInBatches(Iterable<T> params, Consumer<Iterable<T>> consumer) {
        applyInBatches(params, FunctionConverter.toFunction(consumer), BATCH_SIZE, null);
    }

    public static <R, T> List<R> applyInBatches(Iterable<T> params, Function<Iterable<T>, Iterable<R>> function) {
        return applyInBatches(params, function, BATCH_SIZE, null);
    }

    public static <T> void acceptInBatches(Iterable<T> params, Consumer<Iterable<T>> consumer, int batchSize) {
        applyInBatches(params, FunctionConverter.toFunction(consumer), batchSize, null);
    }

    public static <R, T> List<R> applyInBatches(Iterable<T> params, Function<Iterable<T>, Iterable<R>> function, int batchSize) {
        return applyInBatches(params, function, batchSize, null);
    }

    public static <T> void acceptInBatches(Iterable<T> params, Consumer<Iterable<T>> consumer, String name) {
        applyInBatches(params, FunctionConverter.toFunction(consumer), BATCH_SIZE, name);
    }

    public static <R, T> List<R> applyInBatches(Iterable<T> params, Function<Iterable<T>, Iterable<R>> function, String name) {
        return applyInBatches(params, function, BATCH_SIZE, name);
    }

    public static <T> void acceptInBatches(Iterable<T> params, Consumer<Iterable<T>> consumer, int batchSize, String name) {
        applyInBatches(params, FunctionConverter.toFunction(consumer), batchSize, name);
    }

    /**
     * @param params    参数集合
     * @param function  参数集合映射到结果集合的函数
     * @param batchSize 一次处理多少参数
     * @param name      给当前操作起个名字，方便排查问题
     * @return 结果
     */
    public static <R, T> List<R> applyInBatches(Iterable<T> params, Function<Iterable<T>, Iterable<R>> function, int batchSize, String name) {
        if (params == null) {
            return new ArrayList<>();
        }
        if (batchSize <= 0) {
            throw new UtilException("applyInBatches 的 batchSize 必须大于1，当前是: " + batchSize + ". ");
        }
        long start = System.currentTimeMillis();
        List<R> result = new ArrayList<>();
        List<List<T>> paramsList = split(params, batchSize);
        for (int i = 0; i < paramsList.size(); i++) {
            List<T> paramSeg = paramsList.get(i);
            log.debug("Seg-{}-param: {}", i, JacksonUtils.objectToString(paramSeg));
            Iterable<R> resultSeg = function.apply(paramSeg);
            if (!(function instanceof ConsumerFunction)) {
                log.debug("Seg-{}-result: {}", i, JacksonUtils.objectToString(resultSeg));
            }
            if (resultSeg != null) {
                resultSeg.forEach(result::add);
            }
        }
        if (name != null) {
            log.info("applyInBatches [{}] operation execute [{}] ms. ", name, System.currentTimeMillis() - start);
        } else {
            log.info("applyInBatches operation execute [{}] ms. ", System.currentTimeMillis() - start);
        }
        return result;
    }


    public static <T1, T2> void acceptInBatches(
            Iterable<T1> param1s, Iterable<T2> param2s, BiConsumer<Iterable<T1>, Iterable<T2>> consumer) {
        applyInBatches(param1s, param2s, FunctionConverter.toBiFunction(consumer), BATCH_SIZE, null);
    }


    public static <R, T1, T2> List<R> applyInBatches(
            Iterable<T1> param1s, Iterable<T2> param2s, BiFunction<Iterable<T1>, Iterable<T2>, Iterable<R>> function) {
        return applyInBatches(param1s, param2s, function, BATCH_SIZE, null);
    }

    public static <T1, T2> void acceptInBatches(
            Iterable<T1> param1s, Iterable<T2> param2s, BiConsumer<Iterable<T1>, Iterable<T2>> consumer, int batchSize) {
        applyInBatches(param1s, param2s, FunctionConverter.toBiFunction(consumer), batchSize, null);
    }

    public static <R, T1, T2> List<R> applyInBatches(
            Iterable<T1> param1s, Iterable<T2> param2s, BiFunction<Iterable<T1>, Iterable<T2>, Iterable<R>> function, int batchSize) {
        return applyInBatches(param1s, param2s, function, batchSize, null);
    }

    public static <T1, T2> void acceptInBatches(
            Iterable<T1> param1s, Iterable<T2> param2s, BiConsumer<Iterable<T1>, Iterable<T2>> consumer, String name) {
        applyInBatches(param1s, param2s, FunctionConverter.toBiFunction(consumer), BATCH_SIZE, name);
    }

    public static <R, T1, T2> List<R> applyInBatches(
            Iterable<T1> param1s, Iterable<T2> param2s, BiFunction<Iterable<T1>, Iterable<T2>, Iterable<R>> function, String name) {
        return applyInBatches(param1s, param2s, function, BATCH_SIZE, name);
    }

    public static <T1, T2> void acceptInBatches(
            Iterable<T1> param1s, Iterable<T2> param2s, BiConsumer<Iterable<T1>, Iterable<T2>> consumer, int batchSize, String name) {
        applyInBatches(param1s, param2s, FunctionConverter.toBiFunction(consumer), batchSize, name);
    }

    public static <R, T1, T2> List<R> applyInBatches(
            Iterable<T1> param1s, Iterable<T2> param2s, BiFunction<Iterable<T1>, Iterable<T2>, Iterable<R>> function, int batchSize, String name) {
        if (param1s == null || param2s == null) {
            return new ArrayList<>();
        }
        if (batchSize <= 0) {
            throw new UtilException("applyInBatches batchSize 必须大于1，当前是: " + batchSize + ". ");
        }
        long start = System.currentTimeMillis();
        List<R> result = new ArrayList<>();
        List<List<T1>> param1sList = split(param1s, batchSize);
        for (int i = 0; i < param1sList.size(); i++) {
            List<T1> param1Seg = param1sList.get(i);
            List<List<T2>> param2sList = split(param2s, batchSize);
            for (int j = 0; j < param2sList.size(); j++) {
                List<T2> param2Seg = param2sList.get(j);
                log.debug("Seg-{}-{}-param: {}, {}", i, j, JacksonUtils.objectToString(param1Seg), JacksonUtils.objectToString(param2Seg));
                Iterable<R> resultSeg = function.apply(param1Seg, param2Seg);
                if (!(function instanceof BiConsumerBiFunction)) {
                    log.debug("Seg-{}-{}-result: {}", i, j, JacksonUtils.objectToString(resultSeg));
                }
                if (resultSeg != null) {
                    resultSeg.forEach(result::add);
                }
            }
        }
        if (name != null) {
            log.info("applyInBatches {} operation execute [{}] ms. ", name, System.currentTimeMillis() - start);
        } else {
            log.info("applyInBatches operation execute [{}] ms. ", System.currentTimeMillis() - start);
        }
        return result;
    }


    public static <T> List<T> merge(Iterable<T> a, Iterable<T> b) {
        if (a == null && b == null) {
            return new ArrayList<>();
        }
        if (a == null) {
            return toList(b);
        }
        if (b == null) {
            return toList(a);
        }
        List<T> result = new ArrayList<>();
        a.forEach(result::add);
        b.forEach(result::add);
        return result;
    }

    public static <T, R extends Comparable<? super R>> List<T> deDuplicate(Iterable<T> list, Function<T, R> function) {
        return deDuplicate(list, Comparator.comparing(function), ArrayList::new);
    }

    public static <T> List<T> deDuplicate(Iterable<T> list, Comparator<T> comparator) {
        return deDuplicate(list, comparator, ArrayList::new);
    }

    public static <T> List<T> deDuplicate(Iterable<T> list, Comparator<T> comparator, Function<TreeSet<T>, List<T>> finisher) {
        if (list == null) {
            return null;
        }
        return StreamSupport.stream(list.spliterator(), false).collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(comparator)), finisher));
    }

    public static <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    /**
     * 将List集合转为HashSet集合。
     */
    public static <T> Set<T> toHashSet(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * 将List集合转为TreeSet集合。
     */
    public static <T> Set<T> toTreeSet(Iterable<T> iterable, Comparator<T> comparator) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toCollection((Supplier<Set<T>>) () -> new TreeSet<>(comparator)));
    }

    public static <T> List<T> castToList(Iterable<T> iterable) {
        return (List<T>) iterable;
    }

    public static <T> Set<T> castToSet(Iterable<T> iterable) {
        return (Set<T>) iterable;
    }

}
