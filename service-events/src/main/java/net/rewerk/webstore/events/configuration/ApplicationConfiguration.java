package net.rewerk.webstore.events.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Main application configuration class for events microservice
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
    @Primary
    public CacheManager cacheManager() {
        return new CaffeineCacheManager("caffeine");
    }
}
