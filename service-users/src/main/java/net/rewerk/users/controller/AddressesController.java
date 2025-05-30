package net.rewerk.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.address.AddressCreateDto;
import net.rewerk.webstore.dto.request.address.AddressSearchDto;
import net.rewerk.users.service.entity.AddressService;
import net.rewerk.users.specification.AddressSpecification;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.entity.Address;
import net.rewerk.webstore.utility.RequestUtils;
import net.rewerk.webstore.utility.ResponseUtils;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for Address service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/addresses")
public class AddressesController {
    private final AddressService addressService;

    /**
     * GET endpoint for retrieve page of addresses
     *
     * @param addressSearchDto DTO with search parameters
     * @param jwt              OAuth2 jwt token
     * @param authentication   Spring Security Authentication object
     * @return Response with status 200 OK and paginated payload of Address entity response DTO
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<AddressResponseDto>> getAddresses(
            @Valid AddressSearchDto addressSearchDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Page<AddressResponseDto> result = addressService.findAll(
                AddressSpecification.getSpecification(
                        SecurityUtils.getUserFromJwtToken(jwt, authentication),
                        addressSearchDto),
                RequestUtils.getSortAndPageRequest(addressSearchDto)
        );
        return ResponseUtils.createPaginatedResponse(result);
    }

    /**
     * GET endpoint for retrieve list of addresses
     *
     * @param ids List of Address entity identifiers
     * @return Response with status 200 OK and payload with collection of Address entity response DTO
     */

    @GetMapping("by_ids")
    public ResponseEntity<PayloadResponseDto<AddressResponseDto>> getAddressesById(
            @RequestParam List<Integer> ids
    ) {
        return ResponseUtils.createCollectionResponse(addressService.findAllByIds(ids));
    }

    /**
     * POST endpoint for create Address entity
     *
     * @param addressCreateDto DTO with create data
     * @param jwt              OAuth2 jwt token
     * @param uriBuilder       UriComponentsBuilder - autowired for create redirect location
     * @return Response with status 201 Created and payload with single Address entity response DTO
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<AddressResponseDto>> createAddress(
            @Valid @RequestBody AddressCreateDto addressCreateDto,
            @AuthenticationPrincipal Jwt jwt,
            UriComponentsBuilder uriBuilder
    ) {
        AddressResponseDto result = addressService.create(addressCreateDto, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.created(uriBuilder.replacePath("/api/v1/addresses/{addressId}")
                        .build(Map.of("addressId", result.getId())))
                .body(SinglePayloadResponseDto.<AddressResponseDto>builder()
                        .code(HttpStatus.CREATED.value())
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .payload(result)
                        .build());
    }
}
