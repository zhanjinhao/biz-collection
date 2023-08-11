package cn.addenda.bc.bc.sc.idempotent;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author addenda
 * @since 2023/7/28 17:25
 */
@Configuration
public class IdempotentConfiguration implements ImportAware {

    protected AnnotationAttributes annotationAttributes;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.annotationAttributes = AnnotationAttributes.fromMap(
            importMetadata.getAnnotationAttributes(EnableIdempotent.class.getName(), false));
        if (this.annotationAttributes == null) {
            throw new IllegalArgumentException(
                "@EnableIdempotent is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    public IdempotentInterceptor idempotentInterceptor() {
        IdempotentInterceptor idempotentInterceptor = new IdempotentInterceptor();
        idempotentInterceptor.setNamespace(annotationAttributes.getString("namespace"));
        return idempotentInterceptor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public IdempotentAdvisor idempotentAdvisor(IdempotentInterceptor methodInterceptor) {
        IdempotentAdvisor idempotentAdvisor = new IdempotentAdvisor();
        idempotentAdvisor.setAdvice(methodInterceptor);
        idempotentAdvisor.setOrder(annotationAttributes.<Integer>getNumber("order"));
        return idempotentAdvisor;
    }

}
