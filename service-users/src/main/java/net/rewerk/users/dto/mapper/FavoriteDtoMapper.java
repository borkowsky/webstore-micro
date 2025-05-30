package net.rewerk.users.dto.mapper;

import net.rewerk.webstore.dto.request.favorite.FavoriteCreateDto;
import net.rewerk.webstore.dto.response.favorite.FavoriteResponseDto;
import net.rewerk.webstore.entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapstruct DTO mapper for Favorite service
 *
 * @author rewerk
 */

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FavoriteDtoMapper {

    /**
     * Map Favorite create DTO to Favorite entity
     *
     * @param dto Favorite create DTO
     * @return Mapped Favorite entity
     */

    Favorite createDtoToFavorite(FavoriteCreateDto dto);

    /**
     * Map Favorite entity to Favorite response DTO
     *
     * @param favorite Favorite entity
     * @return Mapped Favorite response DTO
     */

    FavoriteResponseDto toResponseDto(Favorite favorite);
}
