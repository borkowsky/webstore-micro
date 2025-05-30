package net.rewerk.webstore.events.service.entity;

import net.rewerk.webstore.dto.mq.events.EventsWriteDto;
import net.rewerk.webstore.dto.response.event.EventResponseDto;
import net.rewerk.webstore.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface EventService {
    Page<EventResponseDto> findAll(Specification<Event> specification, Pageable pageable);

    void create(EventsWriteDto dto);
}
