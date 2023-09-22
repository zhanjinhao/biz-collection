package cn.addenda.bc.bc.jc.allocator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/9/12 20:58
 */
public interface AutoExpiredAllocator<T> extends Allocator<T> {

    T allocate(String name, TimeUnit timeUnit, long timeout);

    @Setter
    @Getter
    @ToString
    @Builder
    class Param {
        private String name;
        private TimeUnit timeUnit;
        private long timeout;
    }

}
