package net.rewerk.webstore.events.service.aggregator;

import net.rewerk.webstore.dto.response.event.EventResponseDto;
import net.rewerk.webstore.entity.Event;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Data aggregation Event service
 * Populate user for Event response DTO
 *
 * @author rewerk
 */

public interface EventsAggregatorService {
    /**
     * Aggregate single Event entity
     *
     * @param event Event entity
     * @return Aggregated response DTO
     */
    EventResponseDto aggregate(Event event);

    /**
     * Aggregate multiple Event entities
     *
     * @param events Event entity
     * @return Aggregated list of response DTO
     */
    List<EventResponseDto> aggregate(List<Event> events);

    /**
     * Aggregate Page of Event entities
     *
     * @param page page of Event entities
     * @return Aggregated page of response DTO
     */
    Page<EventResponseDto> aggregate(Page<Event> page);
}
