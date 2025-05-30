package net.rewerk.webstore.products.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.request.product.ProductUpdateRatingDto;
import net.rewerk.webstore.products.service.entity.ProductService;
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
    private final ProductService productService;

    /**
     * Consumers for update product rating
     *
     * @return Consumer for update product rating
     */

    @Bean
    public Consumer<Message<ProductUpdateRatingDto>> productsUpdateRatingMQConsumer() {
        return message -> {
            log.info("SCS product update rating mq consumer: message consumed with data = {}", message.getPayload());
            productService.updateProductRating(message.getPayload());
        };
    }
}
