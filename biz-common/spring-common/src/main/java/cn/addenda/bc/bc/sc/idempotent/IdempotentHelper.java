package cn.addenda.bc.bc.sc.idempotent;

import cn.addenda.bc.bc.jc.function.TRunnable;
import cn.addenda.bc.bc.jc.function.TSupplier;
import cn.addenda.bc.bc.jc.util.ExceptionUtil;

/**
 * @author addenda
 * @since 2023/7/31 15:00
 */
public class IdempotentHelper extends IdempotentSupport {

    /**
     * 最简单的加锁场景，arguments[0] 是 key
     */
    public <R> R idempotent(IdempotentScenario scenario, TSupplier<R> supplier, Object... arguments) {
        IdempotentAttr attr = IdempotentAttr.builder().scenario(scenario).build();
        return idempotent(attr, supplier, arguments);
    }

    public void idempotent(IdempotentScenario scenario, TRunnable runnable, Object... arguments) {
        IdempotentAttr attr = IdempotentAttr.builder().scenario(scenario).build();
        idempotent(attr, runnable, arguments);
    }

    /**
     * 较上一个场景，arguments[0] 是 key，prefix可以指定
     */
    public <R> R idempotent(IdempotentScenario scenario, String prefix, TSupplier<R> supplier, Object... arguments) {
        IdempotentAttr attr = IdempotentAttr.builder().scenario(scenario).prefix(prefix).build();
        return idempotent(attr, supplier, arguments);
    }

    public void idempotent(IdempotentScenario scenario, String prefix, TRunnable runnable, Object... arguments) {
        IdempotentAttr attr = IdempotentAttr.builder().scenario(scenario).prefix(prefix).build();
        idempotent(attr, runnable, arguments);
    }

    /**
     * 较上一个场景，arguments[0] 是 key，consumeMode和prefix可以指定
     */
    public <R> R idempotent(IdempotentScenario scenario, ConsumeMode consumeMode, String prefix, TSupplier<R> supplier, Object... arguments) {
        IdempotentAttr attr = IdempotentAttr.builder().scenario(scenario).prefix(prefix).consumeMode(consumeMode).build();
        return idempotent(attr, supplier, arguments);
    }

    public void idempotent(IdempotentScenario scenario, ConsumeMode consumeMode, String prefix, TRunnable runnable, Object... arguments) {
        IdempotentAttr attr = IdempotentAttr.builder().scenario(scenario).prefix(prefix).consumeMode(consumeMode).build();
        idempotent(attr, runnable, arguments);
    }

    public void idempotent(IdempotentAttr attr, TRunnable runnable, Object... arguments) {
        idempotent(attr, runnable.toTSupplier(), arguments);
    }

    public <R> R idempotent(IdempotentAttr attr, TSupplier<R> supplier, Object... arguments) {
        if (arguments == null || arguments.length == 0 || attr == null || attr.getScenario() == null || supplier == null) {
            throw new IdempotentException("参数不能为空！");
        }
        try {
            return (R) invokeWithIdempotent(attr, arguments, supplier::get, null);
        } catch (Throwable throwable) {
            throw ExceptionUtil.wrapAsRuntimeException(throwable, IdempotentException.class);
        }
    }

}
