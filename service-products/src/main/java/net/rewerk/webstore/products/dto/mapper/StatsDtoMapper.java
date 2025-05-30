package net.rewerk.webstore.products.dto.mapper;

import net.rewerk.webstore.dto.response.stats.StatsResponseDto;
import net.rewerk.webstore.entity.Stats;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StatsDtoMapper {
    List<StatsResponseDto> toDto(List<Stats> stats);
}
