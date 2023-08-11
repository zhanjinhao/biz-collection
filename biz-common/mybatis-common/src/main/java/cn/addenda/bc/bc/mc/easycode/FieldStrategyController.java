package cn.addenda.bc.bc.mc.easycode;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2023/6/4 23:19
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldStrategyController {

    FieldStrategy strategy() default FieldStrategy.IGNORE;

}
