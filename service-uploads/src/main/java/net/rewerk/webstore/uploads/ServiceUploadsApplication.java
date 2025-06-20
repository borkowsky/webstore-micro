package net.rewerk.webstore.uploads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(
        exclude = {
                DataSourceAutoConfiguration.class
        }
)
@EnableAsync
@EnableDiscoveryClient
public class ServiceUploadsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUploadsApplication.class, args);
    }

}
