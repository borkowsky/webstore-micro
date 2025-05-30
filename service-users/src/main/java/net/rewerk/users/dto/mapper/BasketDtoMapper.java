package net.rewerk.users.dto.mapper;

import net.rewerk.webstore.dto.request.basket.BasketCreateDto;
import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.entity.Basket;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapstruct DTO mapper for Basket service
 *
 * @author rewerk
 */

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BasketDtoMapper {

    /**
     * Map Basket create DTO to Basket entity
     *
     * @param dto Basket create DTO
     * @return Mapped Basket entity
     */

    Basket createDtoToBasket(BasketCreateDto dto);

    /**
     * Map Basket entity to Basket response DTO
     *
     * @param basket Basket entity
     * @return Mapped Basket response DTO
     */

    BasketResponseDto toResponseDto(Basket basket);
}
