package net.rewerk.users.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Main application configuration class for users microservice
 *
 * @author rewerk
 */

@Configuration
public class ApplicationConfiguration {

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
