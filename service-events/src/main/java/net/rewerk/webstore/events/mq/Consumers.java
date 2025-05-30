package net.rewerk.webstore.events.mq;

import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.mq.events.EventsWriteDto;
import net.rewerk.webstore.events.service.entity.EventService;
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
public class Consumers {
    private final EventService eventService;

    /**
     * Consumer for writing events
     *
     * @return Consumer for write event
     */

    @Bean
    public Consumer<Message<EventsWriteDto>> eventsWriteConsumer() {
        return message -> eventService.create(message.getPayload());
    }
}
