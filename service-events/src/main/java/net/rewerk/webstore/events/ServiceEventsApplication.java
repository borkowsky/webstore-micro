package net.rewerk.webstore.events;

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
@EnableAsync
@EnableTransactionManagement
@PropertySource("classpath:application.yml")
@EntityScan("net.rewerk.webstore.entity")
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
@EnableFeignClients
public class ServiceEventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceEventsApplication.class, args);
    }

}
