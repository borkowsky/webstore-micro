package net.rewerk.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.users.service.entity.BasketService;
import net.rewerk.users.specification.BasketSpecification;
import net.rewerk.webstore.dto.request.basket.BasketCreateDto;
import net.rewerk.webstore.dto.request.basket.BasketMultipleDeleteDto;
import net.rewerk.webstore.dto.request.basket.BasketSearchDto;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
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
 * REST controller for Basket service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/basket")
public class BasketsController {
    private final BasketService basketService;

    /**
     * GET endpoint for retrieve page of baskets
     *
     * @param searchDto      DTO with search parameters
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Response with status 200 OK and payload with page of Basket entity response DTO
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<BasketResponseDto>> findAllBaskets(
            @Valid BasketSearchDto searchDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Page<BasketResponseDto> result = basketService.findAll(
                BasketSpecification.getSpecification(
                        searchDto,
                        SecurityUtils.getUserFromJwtToken(jwt, authentication)
                ),
                RequestUtils.getPageRequest(searchDto)
        );
        return ResponseUtils.createPaginatedResponse(result);
    }

    /**
     * GET endpoint for retrieve list of baskets by identifiers
     *
     * @param ids List of Basket identifiers
     * @return Response with status 200 OK and payload with list of Basket entity response DTO
     */

    @GetMapping("by_ids")
    public ResponseEntity<PayloadResponseDto<BasketResponseDto>> getBasketByIds(
            @RequestParam List<Integer> ids
    ) {
        return ResponseUtils.createCollectionResponse(basketService.findAllById(ids));
    }

    /**
     * GET endpoint for sync browser local basket
     *
     * @param productIds     List of Product identifiers
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Response with status 204 No Content without payload
     */

    @GetMapping("sync")
    public ResponseEntity<Void> sync(
            @RequestParam(name = "product_id") List<Integer> productIds,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        basketService.sync(productIds, SecurityUtils.getUserFromJwtToken(jwt, authentication));
        return ResponseEntity.noContent().build();
    }

    /**
     * GET endpoint for retrieve list of categories from user basket products
     *
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Response with status 200 OK and payload with collection of Category entity response DTO
     */

    @GetMapping("/categories")
    public ResponseEntity<PayloadResponseDto<CategoryResponseDto>> findBasketCategories(
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        List<CategoryResponseDto> categories = basketService
                .findAllCategories(BasketSpecification
                        .getSpecification(SecurityUtils.getUserFromJwtToken(jwt, authentication))
                );
        return ResponseUtils.createCollectionResponse(categories);
    }

    /**
     * POST endpoint for create Basket entity
     *
     * @param request    DTO with create data
     * @param uriBuilder UriComponentsBuilder - autowired for create redirect location
     * @param jwt        OAuth2 jwt token
     * @return Response with status 204 No Content without payload
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<BasketResponseDto>> createBasket(
            @Valid @RequestBody BasketCreateDto request,
            UriComponentsBuilder uriBuilder,
            @AuthenticationPrincipal Jwt jwt
    ) {
        BasketResponseDto result = basketService.create(request, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.created(uriBuilder.replacePath("/api/v1/basket/{basketId}").build(
                        Map.of("basketId", result.getId())
                ))
                .body(SinglePayloadResponseDto.<BasketResponseDto>builder()
                        .code(HttpStatus.CREATED.value())
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .payload(result)
                        .build());
    }

    /**
     * DELETE endpoint for delete multiple Basket entities
     *
     * @param request        DTO with batch delete parameters
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteBaskets(
            @Valid @RequestBody BasketMultipleDeleteDto request,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        basketService.deleteAllById(request, SecurityUtils.getUserFromJwtToken(jwt, authentication));
        return ResponseEntity.noContent().build();
    }
}
