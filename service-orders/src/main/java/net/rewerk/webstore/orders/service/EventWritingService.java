package net.rewerk.webstore.orders.service;

import net.rewerk.webstore.dto.mq.events.EventsWriteDto;
import net.rewerk.webstore.orders.mq.Suppliers;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Sinks;

import java.util.UUID;

/**
 * Service for inherit, contains method for send SCS messages to event writing queue
 *
 * @author rewerk
 */

public class EventWritingService {

    /**
     * Method for send SCS message to event writing queue
     *
     * @param suppliers SCS suppliers class
     * @param text      Text to send for new event
     */

    protected void writeEvent(Suppliers suppliers, String text) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnsupportedOperationException("No authentication found");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        suppliers.getEventsWriteSink().emitNext(MessageBuilder.withPayload(EventsWriteDto.builder()
                .user_id(UUID.fromString(jwt.getSubject()))
                .text(text)
                .build()
        ).build(), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
