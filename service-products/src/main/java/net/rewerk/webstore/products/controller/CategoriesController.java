package net.rewerk.webstore.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.category.CategoryCreateDto;
import net.rewerk.webstore.dto.request.category.CategorySearchDto;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.products.service.entity.CategoryService;
import net.rewerk.webstore.products.specification.CategorySpecification;
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

/**
 * Not parametrized REST controller for Category service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController {
    private final CategoryService categoryService;

    /**
     * GET endpoint for find categories by search criteria
     *
     * @param requestParams  DTO with search parameters
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security authentication object
     * @return Response with status 200 OK and payload with page of Category entities
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<CategoryResponseDto>> findAllCategories(
            @Valid CategorySearchDto requestParams,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Page<CategoryResponseDto> categories = categoryService.findAll(
                CategorySpecification
                        .getSpecification(requestParams, SecurityUtils.getUserFromJwtToken(jwt, authentication)),
                RequestUtils.getSortAndPageRequest(requestParams)
        );
        return ResponseUtils.createPaginatedResponse(categories);
    }

    /**
     * GET endpoint for retrieve list of Category entities by Product identifiers
     *
     * @param productIds List of Product identifiers
     * @return Response with status 200 OK and payload with collection of Category entities response DTO
     */

    @GetMapping("by_products")
    public ResponseEntity<PayloadResponseDto<CategoryResponseDto>> findAllCategoriesByProducts(
            @RequestParam List<Integer> productIds
    ) {
        List<CategoryResponseDto> allDistinctByProductIdIn = categoryService.findAllDistinctByProductIdIn(productIds);
        return ResponseUtils.createCollectionResponse(allDistinctByProductIdIn);
    }

    /**
     * POST endpoint for create Category entity
     *
     * @param request    DTO with create data
     * @param uriBuilder UriComponentsBuilder - autowired for create redirect
     * @return Response with status 201 Created and single payload with created Category entity response DTO
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<CategoryResponseDto>> createCategory(
            @Valid @RequestBody CategoryCreateDto request,
            UriComponentsBuilder uriBuilder
    ) {
        CategoryResponseDto result = categoryService.create(request);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/v1/categories/{categoryId}")
                        .build(Map.of("categoryId", result.getId()))
                )
                .body(SinglePayloadResponseDto.<CategoryResponseDto>builder()
                        .code(HttpStatus.CREATED.value())
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .payload(result)
                        .build());
    }
}
