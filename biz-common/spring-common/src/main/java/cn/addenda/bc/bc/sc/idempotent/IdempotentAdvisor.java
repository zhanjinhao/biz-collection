package cn.addenda.bc.bc.sc.idempotent;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * @author addenda
 * @since 2022/9/29 13:52
 */
public class IdempotentAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    @Override
    public Pointcut getPointcut() {
        return new IdempotentPointcut();
    }

    public static class IdempotentPointcut extends StaticMethodMatcherPointcut {

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            Idempotent annotation = AnnotationUtils.findAnnotation(targetClass, Idempotent.class);
            if (annotation == null) {
                Method actualMethod = AopUtils.getMostSpecificMethod(method, targetClass);
                annotation = AnnotationUtils.findAnnotation(actualMethod, Idempotent.class);
            }

            return annotation != null;
        }

    }

}
