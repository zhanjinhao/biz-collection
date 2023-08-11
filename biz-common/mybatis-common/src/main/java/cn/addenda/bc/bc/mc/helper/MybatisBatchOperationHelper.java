package cn.addenda.bc.bc.mc.helper;

import cn.addenda.bc.bc.jc.function.FunctionConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

/**
 * @author addenda
 * @since 2022/10/9 16:57
 */
@Slf4j
public class MybatisBatchOperationHelper {

    private static final int BATCH_SIZE = 100;

    private final SqlSessionFactory sqlSessionFactory;

    public MybatisBatchOperationHelper(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public <T, U> void batch(Class<T> mapperClass, Iterable<U> data, BiConsumer<T, U> consumer) {
        batch(mapperClass, data, FunctionConverter.toBiFunction(consumer), getVoidMerger());
    }

    public <T, U> void batch(Class<T> mapperClass, Iterable<U> data, BiConsumer<T, U> consumer, int batchSize) {
        batch(mapperClass, data, FunctionConverter.toBiFunction(consumer), getVoidMerger(), batchSize);
    }

    public <T, U> void batch(Class<T> mapperClass, Iterable<U> data, BiConsumer<T, U> consumer, String name) {
        batch(mapperClass, data, FunctionConverter.toBiFunction(consumer), getVoidMerger(), name);
    }

    public <T, U> void batch(Class<T> mapperClass, Iterable<U> data, BiConsumer<T, U> consumer, Integer batchSize, String name) {
        batch(mapperClass, data, FunctionConverter.toBiFunction(consumer), getVoidMerger(), batchSize, name);
    }

    public <T, U, R> R batch(Class<T> mapperClass, Iterable<U> data, BiFunction<T, U, R> function, BinaryOperator<R> merger) {
        return batch(mapperClass, data, function, merger, BATCH_SIZE, null);
    }

    public <T, U, R> R batch(Class<T> mapperClass, Iterable<U> data, BiFunction<T, U, R> function, BinaryOperator<R> merger, String name) {
        return batch(mapperClass, data, function, merger, BATCH_SIZE, name);
    }

    public <T, U, R> R batch(Class<T> mapperClass, Iterable<U> data, BiFunction<T, U, R> function, BinaryOperator<R> merger, Integer batchSize) {
        return batch(mapperClass, data, function, merger, batchSize, null);
    }

    /**
     * 批量处理 DML 语句
     *
     * @param data        需要被处理的数据
     * @param mapperClass Mybatis的Mapper类
     * @param function    自定义处理逻辑
     * @param merger      合并单次function的接口
     * @param batchSize   flush到db的最大值
     * @param name        给batch操作起个名，方便排查问题
     */
    public <T, U, R> R batch(Class<T> mapperClass, Iterable<U> data, BiFunction<T, U, R> function, BinaryOperator<R> merger, Integer batchSize, String name) {
        if (data == null) {
            return null;
        }
        if (batchSize == null) {
            batchSize = BATCH_SIZE;
        }
        long start = System.currentTimeMillis();
        R pre = null;
        try (SqlSession batchSqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            int i = 0;
            T mapper = batchSqlSession.getMapper(mapperClass);
            for (U element : data) {
                R apply = function.apply(mapper, element);
                pre = merger.apply(pre, apply);
                if (i != 0 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            batchSqlSession.flushStatements();
        }
        if (name == null) {
            log.info("batch operation execute [{}] ms. ", System.currentTimeMillis() - start);
        } else {
            log.info("batch {} operation execute [{}] ms. ", name, System.currentTimeMillis() - start);
        }
        return pre;
    }

    private static <R> BinaryOperator<R> getVoidMerger() {
        return (r, r2) -> null;
    }

}
