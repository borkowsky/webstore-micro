package net.rewerk.webstore.reviews.mq;

import lombok.Getter;
import net.rewerk.webstore.dto.mq.events.EventsWriteDto;
import net.rewerk.webstore.dto.request.product.ProductUpdateRatingDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.function.Supplier;

/**
 * Spring Cloud Streams suppliers
 *
 * @author rewerk
 */

@Configuration
@Getter
public class Suppliers {

    /**
     * Reactive Sinks for cast events to writeEventsMQProducer
     */

    private final Sinks.Many<Message<EventsWriteDto>> eventsWriteSink = Sinks.many()
            .multicast()
            .onBackpressureBuffer(
                    Queues.SMALL_BUFFER_SIZE,
                    false
            );

    /**
     * Reactive Sinks for cast events to updateProductRatingMQProducer
     */

    private final Sinks.Many<Message<ProductUpdateRatingDto>> productUpdateRatingSink = Sinks.many()
            .multicast()
            .onBackpressureBuffer(
                    Queues.SMALL_BUFFER_SIZE,
                    false
            );

    /**
     * Supplier for produce data to write events SCS queue
     *
     * @return Supplier for generate data to SCS queue
     */

    @Bean
    public Supplier<Flux<Message<EventsWriteDto>>> writeEventsMQProducer() {
        return this.eventsWriteSink::asFlux;
    }

    /**
     * Supplier for produce data to update product rating SCS queue
     *
     * @return Supplier for generate data to SCS queue
     */

    @Bean
    public Supplier<Flux<Message<ProductUpdateRatingDto>>> updateProductRatingMQProducer() {
        return this.productUpdateRatingSink::asFlux;
    }
}
