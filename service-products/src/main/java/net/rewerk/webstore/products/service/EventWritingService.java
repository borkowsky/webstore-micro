package net.rewerk.webstore.products.service;

import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.mq.events.EventsWriteDto;
import net.rewerk.webstore.products.mq.Suppliers;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Slf4j
public class EventWritingService {
    public void writeEvent(Suppliers suppliers, String text) {
        log.info("EventWritingService.writeEvent: send write event message to SCS queue with text = {}", text);
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
