package cn.addenda.bc.bc.sc.idempotent;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author addenda
 * @since 2023/7/28 17:26
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * {@link IdempotentAttr#getPrefix()}
     */
    String prefix() default IdempotentAttr.DEFAULT_PREFIX;

    /**
     * {@link IdempotentAttr#getSpEL()}
     */
    String spEL() default "";

    /**
     * {@link IdempotentAttr#getRepeatConsumptionMsg()}
     */
    String repeatConsumptionMsg() default IdempotentAttr.DEFAULT_REPEAT_CONSUMPTION_MSG;

    /**
     * {@link IdempotentAttr#getScenario()}
     */
    IdempotentScenario scenario();

    /**
     * {@link IdempotentAttr#getStorageCenter()} 。存储中心的bean。用于判断是否处理过。
     */
    String storageCenter() default IdempotentAttr.DEFAULT_STORAGE_CENTER;

    /**
     * {@link IdempotentAttr#getConsumeMode()}
     */
    ConsumeMode consumeMode() default ConsumeMode.SUCCESS;

    /**
     * {@link IdempotentAttr#getTimeUnit()}
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * {@link IdempotentAttr#getExpectedCost()}
     */
    int expectedCost() default IdempotentAttr.DEFAULT_EXPECTED_COST;

    /**
     * {@link IdempotentAttr#getTimeout()}
     */
    int timeout() default IdempotentAttr.DEFAULT_TIME_OUT;

}
