package net.rewerk.users.feign.configuration;

import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DefaultConfiguration {
    @Bean
    ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder();
    }
}
