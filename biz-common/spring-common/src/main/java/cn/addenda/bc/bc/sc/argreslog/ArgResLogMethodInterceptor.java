package cn.addenda.bc.bc.sc.argreslog;

import cn.addenda.bc.bc.jc.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
@Slf4j
public class ArgResLogMethodInterceptor extends ArgResLogSupport implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = AopUtils.getMostSpecificMethod(invocation.getMethod(), invocation.getThis().getClass());
        ArgResLog argResLog = AnnotationUtils.findAnnotation(method, ArgResLog.class);
        if (argResLog == null) {
            return invocation.proceed();
        }

        // 从AOP过来的，参数以及返回值无法做到函数式那样详细。
        // 但Spring AOP的粒度是方法，所以类名+方法名已够用。
        String callerInfo = method.getDeclaringClass().getSimpleName() + "#" + method.getName();

        ArgResLogAttr attr = ArgResLogAttr.builder()
                .logParam(argResLog.logParam())
                .logResult(argResLog.logResult())
                .logCost(argResLog.logCost())
                .ignoreNullResult(argResLog.ignoreNullResult())
                .exceptionLevel(argResLog.exceptionLevel()).build();

        try {
            return invoke(attr, invocation.getArguments(), invocation::proceed, callerInfo);
        } catch (Throwable throwable) {
            throw ExceptionUtil.unwrapThrowable(throwable);
        }
    }

}
