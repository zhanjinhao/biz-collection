package cn.addenda.bc.bc.sc.idempotent;

import cn.addenda.bc.bc.jc.util.ExceptionUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:51
 */
public class IdempotentInterceptor extends IdempotentSupport implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> aClass = invocation.getThis().getClass();
        Idempotent annotation = AnnotationUtils.findAnnotation(aClass, Idempotent.class);
        if (annotation == null) {
            Method method = AopUtils.getMostSpecificMethod(invocation.getMethod(), aClass);
            annotation = AnnotationUtils.findAnnotation(method, Idempotent.class);
        }
        if (annotation == null) {
            return invocation.proceed();
        }

        IdempotentAttr attr = IdempotentAttr.builder()
                .prefix(annotation.prefix())
                .spEL(annotation.spEL())
                .repeatConsumptionMsg(annotation.repeatConsumptionMsg())
                .scenario(annotation.scenario())
                .storageCenter(annotation.storageCenter())
                .consumeMode(annotation.consumeMode())
                .timeUnit(annotation.timeUnit())
                .expectedCost(annotation.expectedCost())
                .timeout(annotation.timeout())
                .build();

        try {
            return invokeWithIdempotent(attr, invocation.getArguments(), invocation::proceed, invocation.getMethod());
        } catch (Throwable throwable) {
            throw ExceptionUtil.unwrapThrowable(throwable);
        }
    }

}
