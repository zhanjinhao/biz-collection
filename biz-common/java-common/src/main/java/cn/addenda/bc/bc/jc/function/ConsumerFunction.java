package cn.addenda.bc.bc.jc.function;

import java.util.function.Consumer;
import java.util.function.Function;

public class ConsumerFunction<T, R> implements Function<T, R> {

    private final Consumer<T> consumer;

    public ConsumerFunction(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public R apply(T o) {
        consumer.accept(o);
        return null;
    }
}
