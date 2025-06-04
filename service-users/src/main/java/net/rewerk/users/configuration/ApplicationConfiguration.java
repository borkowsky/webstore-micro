package net.rewerk.users.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

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
