package net.rewerk.webstore.orders.mq;

import lombok.Getter;
import net.rewerk.webstore.dto.mq.events.EventsWriteDto;
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
     * Supplier for produce data to write events SCS queue
     *
     * @return Supplier for generate data to SCS queue
     */

    @Bean
    public Supplier<Flux<Message<EventsWriteDto>>> writeEventsMQProducer() {
        return this.eventsWriteSink::asFlux;
    }
}
