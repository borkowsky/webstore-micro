package net.rewerk.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.users.dto.mapper.AddressDtoMapper;
import net.rewerk.webstore.dto.request.address.AddressPatchDto;
import net.rewerk.users.service.entity.AddressService;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.entity.Address;
import net.rewerk.webstore.utility.ResponseUtils;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Parametrized REST controller for Address service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/addresses/{id:\\d+}")
public class AddressController {
    private final AddressService addressService;
    private final AddressDtoMapper addressDtoMapper;

    /**
     * Method for populate address method attribute by Address identifier request mapping path variable
     *
     * @param id             Address identifier
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security authentication object
     * @return Address entity
     */

    @ModelAttribute("address")
    public Address getAddress(@PathVariable Integer id,
                              @AuthenticationPrincipal Jwt jwt,
                              Authentication authentication
    ) {
        return addressService.findById(id, SecurityUtils.getUserFromJwtToken(jwt, authentication));
    }

    /**
     * GET endpoint for retrieve address by identifier
     *
     * @param address Model attribute - address
     * @return Response with status 200 OK and payload with single Address entity response DTO
     */

    @GetMapping
    public ResponseEntity<SinglePayloadResponseDto<AddressResponseDto>> getAddress(@ModelAttribute Address address) {
        return ResponseUtils.createSingleResponse(addressDtoMapper.toDto(address));
    }

    /**
     * PATCH endpoint for update Address entity
     *
     * @param addressPatchDto DTO with patch data
     * @param jwt             OAuth2 jwt token
     * @param address         Address entity to update
     * @return Response with status 204 No Content without payload
     */

    @PatchMapping
    public ResponseEntity<Void> updateAddress(@Valid @RequestBody AddressPatchDto addressPatchDto,
                                              @AuthenticationPrincipal Jwt jwt,
                                              @ModelAttribute("address") Address address
    ) {
        addressService.update(address, addressPatchDto, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE endpoint for delete Address entity
     *
     * @param jwt     OAuth2 jwt token
     * @param address Address entity to delete
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal Jwt jwt,
                                              @ModelAttribute("address") Address address) {
        addressService.delete(address, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }
}
