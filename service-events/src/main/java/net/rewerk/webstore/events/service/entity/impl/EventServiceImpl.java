package net.rewerk.webstore.events.service.entity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.mq.events.EventsWriteDto;
import net.rewerk.webstore.dto.response.event.EventResponseDto;
import net.rewerk.webstore.entity.Event;
import net.rewerk.webstore.events.repository.EventRepository;
import net.rewerk.webstore.events.service.aggregator.EventsAggregatorService;
import net.rewerk.webstore.events.service.entity.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Event entity service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventsAggregatorService aggregatorService;

    /**
     * Find all events by parameters
     *
     * @param specification Event JPA Specification
     * @param pageable      Event Pageable request
     * @return Page of aggregated Event entity response DTO
     */
    @Override
    public Page<EventResponseDto> findAll(Specification<Event> specification, Pageable pageable) {
        log.info("EventServiceImpl.findAll: Finding all events with specification");
        return aggregatorService.aggregate(eventRepository.findAll(specification, pageable));
    }

    /**
     * Create Event entity
     *
     * @param dto DTO with create data
     */
    @Override
    public void create(EventsWriteDto dto) {
        log.info("EventServiceImpl.create: Creating event with parameters {}", dto);
        eventRepository.save(Event.builder()
                .userId(dto.getUser_id())
                .text(dto.getText())
                .build());
    }
}
