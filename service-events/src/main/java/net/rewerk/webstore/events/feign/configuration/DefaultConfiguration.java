package net.rewerk.webstore.events.feign.configuration;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign configuration class
 *
 * @author rewerk
 */

@Configuration
public class DefaultConfiguration {

    /**
     * Error decoder configuration bean
     *
     * @return Feign error decoder
     */

    @Bean
    ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder();
    }
}
