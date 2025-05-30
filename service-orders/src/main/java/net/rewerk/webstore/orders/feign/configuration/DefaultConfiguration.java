package net.rewerk.webstore.orders.feign.configuration;

import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultConfiguration {
    @Bean
    ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder();
    }

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }
}
