package net.rewerk.webstore.uploads.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.function.Function;

/**
 * Main application configuration class for uploads microservice
 *
 * @author rewerk
 */

@Configuration
public class ApplicationConfiguration {

    /**
     * Configure primary Bean validator object
     *
     * @return Bean validator
     */

    @Bean
    @Primary
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }


    /**
     * Configure application message source for retrieve localized messages
     *
     * @return Message source
     */

    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Configure application primary Cache manager
     *
     * @return Cache manager
     */

    @Bean
    public Function<String, String> uppercase() {
        return String::toUpperCase;
    }
}
