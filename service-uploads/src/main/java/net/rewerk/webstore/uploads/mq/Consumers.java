package net.rewerk.webstore.uploads.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.mq.uploads.UploadsDeleteObjectsDto;
import net.rewerk.webstore.uploads.service.UploadService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

/**
 * Spring Cloud Streams consumers
 *
 * @author rewerk
 */


@RequiredArgsConstructor
@Configuration
@Slf4j
public class Consumers {
    private final UploadService uploadService;

    /**
     * Consumer for delete object from Google Cloud Storage
     *
     * @return Consumer for delete GCS object
     */

    @Bean
    public Consumer<Message<UploadsDeleteObjectsDto>> deleteUploadObjectConsumer() {
        return message -> {
            log.info("SCS deleteUploadObjectConsumer: deleting object {}", message.getPayload());
            try {
                uploadService.deleteObjects(message.getPayload());
            } catch (InterruptedException e) {
                log.error("SCS deleteUploadObjectConsumer: interrupted while deleting object: error {}", e.getMessage());
            }
        };
    }
}
