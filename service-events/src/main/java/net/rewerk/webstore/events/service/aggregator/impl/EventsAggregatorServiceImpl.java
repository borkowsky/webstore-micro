package net.rewerk.webstore.events.service.aggregator.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.event.EventResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.entity.Event;
import net.rewerk.webstore.events.dto.mapper.EventDtoMapper;
import net.rewerk.webstore.events.feign.client.UsersFeignClient;
import net.rewerk.webstore.events.service.aggregator.EventsAggregatorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsAggregatorServiceImpl implements EventsAggregatorService {
    private final EventDtoMapper eventDtoMapper;
    private final UsersFeignClient usersFeignClient;

    @Override
    public EventResponseDto aggregate(Event event) {
        log.info("EventsAggregatorServiceImpl.aggregate: Aggregating event: ID {}", event.getId());
        SinglePayloadResponseDto<UserResponseDto> userPayload = usersFeignClient.getById(event.getUserId());
        EventResponseDto mapped = eventDtoMapper.toDto(event);
        mapped.setUser(userPayload.getPayload());
        return mapped;
    }

    @Override
    public List<EventResponseDto> aggregate(List<Event> events) {
        log.info("EventsAggregatorServiceImpl.aggregate: Aggregating events: SIZE {}", events.size());
        List<UUID> userIds = events.stream()
                .map(Event::getUserId)
                .distinct()
                .toList();
        PayloadResponseDto<UserResponseDto> usersPayload = usersFeignClient.getByIds(userIds);
        return events.stream()
                .map(eventDtoMapper::toDto)
                .peek(evt -> {
                    Optional<UserResponseDto> user = usersPayload.getPayload().stream()
                            .filter(u -> evt.getUserId().equals(u.getId()))
                            .findFirst();
                    user.ifPresent(evt::setUser);
                })
                .toList();
    }

    @Override
    public Page<EventResponseDto> aggregate(Page<Event> page) {
        log.info("EventsAggregatorServiceImpl.aggregate: Aggregating page of events: CONTENT SIZE {}", page.getSize());
        return new PageImpl<>(this.aggregate(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
