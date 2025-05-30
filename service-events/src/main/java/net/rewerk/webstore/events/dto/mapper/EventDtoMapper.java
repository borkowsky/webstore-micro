package net.rewerk.webstore.events.dto.mapper;

import net.rewerk.webstore.dto.response.event.EventResponseDto;
import net.rewerk.webstore.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EventDtoMapper {
    EventResponseDto toDto(Event event);

    List<EventResponseDto> toDto(List<Event> events);
}
