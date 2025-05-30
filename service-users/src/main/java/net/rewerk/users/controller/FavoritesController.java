package net.rewerk.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.favorite.FavoriteCreateDto;
import net.rewerk.webstore.dto.request.favorite.FavoriteSearchDto;
import net.rewerk.users.service.entity.FavoriteService;
import net.rewerk.users.specification.FavoriteSpecification;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.dto.response.favorite.FavoriteResponseDto;
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
 * REST controller for Favorite service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/favorites")
public class FavoritesController {
    private final FavoriteService favoriteService;

    /**
     * GET endpoint for retrieve page of favorites
     *
     * @param searchDto      DTO with search parameters
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Response with status 200 OK and payload with page of Favorite entity response DTO
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<FavoriteResponseDto>> findAllFavorites(
            @Valid FavoriteSearchDto searchDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Page<FavoriteResponseDto> result = favoriteService.findAll(
                FavoriteSpecification.getSpecification(
                        searchDto,
                        SecurityUtils.getUserFromJwtToken(jwt, authentication)
                ),
                RequestUtils.getSortAndPageRequest(searchDto)
        );
        return ResponseUtils.createPaginatedResponse(result);
    }

    /**
     * GET endpoint for retrieve list if categories for user favorite products list
     *
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Response with status 200 OK and payload with list of Category entity response DTO
     */

    @GetMapping("/categories")
    public ResponseEntity<PayloadResponseDto<CategoryResponseDto>> findFavoriteCategories(
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        List<CategoryResponseDto> categories = favoriteService
                .findAllCategories(
                        FavoriteSpecification.getSpecification(
                                SecurityUtils.getUserFromJwtToken(jwt, authentication)
                        )
                );
        return ResponseUtils.createCollectionResponse(categories);
    }

    /**
     * POST endpoint for create Favorite entity
     *
     * @param request    DTO with create data
     * @param uriBuilder UriComponentsBuilder - autowired for create redirect location
     * @param jwt        OAuth2 jwt token
     * @return Response with status 204 No Content without payload
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<FavoriteResponseDto>> createFavorite(
            @Valid @RequestBody FavoriteCreateDto request,
            UriComponentsBuilder uriBuilder,
            @AuthenticationPrincipal Jwt jwt
    ) {
        FavoriteResponseDto result = favoriteService.create(request, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.created(uriBuilder
                .replacePath("/api/v1/favorites/{favoriteId}")
                .build(Map.of("favoriteId", result.getId()))
        ).body(SinglePayloadResponseDto.<FavoriteResponseDto>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(result)
                .build());
    }
}
