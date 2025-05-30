package net.rewerk.webstore.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableTransactionManagement
@PropertySource("classpath:application.yml")
@EntityScan("net.rewerk.webstore.entity")
@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
@EnableFeignClients
public class ServiceProductsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceProductsApplication.class, args);
    }
}
