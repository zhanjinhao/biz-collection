package cn.addenda.bc.bc.sc.idempotent;

import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author addenda
 * @since 2023/7/28 17:24
 */
public class IdempotentSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
            AutoProxyRegistrar.class.getName(),
            IdempotentConfiguration.class.getName()};
    }

}
