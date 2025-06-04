package net.rewerk.webstore.products.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Duration;

/**
 * Main application configuration class for products microservice
 *
 * @author rewerk
 */

@Configuration
@EnableJpaAuditing
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
    public CacheManager cacheManager(
            @Value("${cache.ttl_minutes:10}") Integer ttl,
            @Value("${cache.size_limit:1000}") Integer size_limit
    ) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("caffeine");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(ttl))
                .maximumSize(size_limit)
        );
        return cacheManager;
    }
}
