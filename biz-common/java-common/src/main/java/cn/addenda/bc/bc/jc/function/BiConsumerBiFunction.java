package cn.addenda.bc.bc.jc.function;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class BiConsumerBiFunction<T, U, R> implements BiFunction<T, U, R> {

    private final BiConsumer<T, U> consumer;

    public BiConsumerBiFunction(BiConsumer<T, U> consumer) {
        this.consumer = consumer;
    }

    @Override
    public R apply(T t, U u) {
        consumer.accept(t, u);
        return null;
    }
}
