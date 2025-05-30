package net.rewerk.users.dto.mapper;

import net.rewerk.webstore.dto.request.address.AddressCreateDto;
import net.rewerk.webstore.dto.request.address.AddressPatchDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * Mapstruct DTO mapper for Address service
 *
 * @author rewerk
 */

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AddressDtoMapper {

    /**
     * Map Address create DTO to Address entity
     *
     * @param dto Address create DTO
     * @return Mapped Address entity
     */

    Address createDtoToAddress(AddressCreateDto dto);

    /**
     * Map Address patch DTO to existing Address entity
     *
     * @param address Existing Address entity
     * @param dto     DTO with patch data
     * @return Mapped Address entity
     */

    Address patchDtoToAddress(@MappingTarget Address address, AddressPatchDto dto);

    /**
     * Map Address entity to Address response DTO
     *
     * @param address Address entity
     * @return Mapped Address response DTO
     */

    AddressResponseDto toDto(Address address);

    /**
     * Map list of Address entity to list of Address response DTO
     *
     * @param addresses List of Address entities
     * @return List of mapped Address response DTO
     */

    List<AddressResponseDto> toDto(List<Address> addresses);
}
