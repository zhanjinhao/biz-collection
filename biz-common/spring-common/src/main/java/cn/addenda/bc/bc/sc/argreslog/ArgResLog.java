package cn.addenda.bc.bc.sc.argreslog;

import java.lang.annotation.*;

/**
 * @author addenda
 * @since 2022/9/29 14:00
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ArgResLog {

    /**
     * 是否打印参数
     */
    boolean logParam() default ArgResLogAttr.DEFAULT_LOG_PARAM;

    /**
     * 是否打印结果
     */
    boolean logResult() default ArgResLogAttr.DEFAULT_LOG_COST;

    /**
     * 是否打印耗时
     */
    boolean logCost() default ArgResLogAttr.DEFAULT_LOG_COST;

    /**
     * 如果结果为空，是否不打印结果
     */
    boolean ignoreNullResult() default ArgResLogAttr.DEFAULT_IGNORE_NULL_RESULT;

    /**
     * 如果结果异常，异常日志的级别
     */
    String exceptionLevel() default ArgResLogAttr.DEFAULT_EXCEPTION_LEVEL;

}
