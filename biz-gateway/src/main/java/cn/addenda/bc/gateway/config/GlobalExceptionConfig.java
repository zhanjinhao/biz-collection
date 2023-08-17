package cn.addenda.bc.gateway.config;

import cn.addenda.bc.gateway.exception.ControllerResultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2023/8/17 20:06
 */
@Configuration
public class GlobalExceptionConfig {

    @Bean
    public ControllerResultErrorAttributes errorAttributes() {
        return new ControllerResultErrorAttributes();
    }

}
