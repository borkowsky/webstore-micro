package net.rewerk.users.dto.mapper;

import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapstruct DTO mapper for User service
 *
 * @author rewerk
 */

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserDtoMapper {

    /**
     * Map User entity to User response DTO
     *
     * @param user User entity
     * @return Mapped User response DTO
     */

    UserResponseDto toResponseDto(User user);
}
