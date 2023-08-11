package cn.addenda.bc.bc.jc.function;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author addenda
 * @since 2023/6/4 15:01
 */
public class FunctionConverter {

    private FunctionConverter() {
    }

    public static <T, R> Function<T, R> toFunction(Consumer<T> consumer) {
        return new ConsumerFunction<>(consumer);
    }

    public static <T, U, R> BiFunction<T, U, R> toBiFunction(BiConsumer<T, U> biConsumer) {
        return new BiConsumerBiFunction<>(biConsumer);
    }

}
