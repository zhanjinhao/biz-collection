package cn.addenda.bc.bc.sc.idempotent;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2022/9/29 13:55
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IdempotentSelector.class)
public @interface EnableIdempotent {

    String namespace() default "idempotent";

    int order() default Ordered.LOWEST_PRECEDENCE;

    boolean proxyTargetClass() default false;

    AdviceMode mode() default AdviceMode.PROXY;

}
