package net.rewerk.webstore.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.product.ProductBatchPatchRequestDto;
import net.rewerk.webstore.dto.request.product.ProductCreateDto;
import net.rewerk.webstore.dto.request.product.ProductSearchDto;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.products.service.entity.ProductService;
import net.rewerk.webstore.products.specification.ProductSpecification;
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
 * Not parametrized REST controller for Product service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {
    private final ProductService productService;

    /**
     * GET endpoint for find products by search criteria
     *
     * @param request        DTO with search parameters
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security authentication object
     * @return Response with status 200 OK and payload with page of ProductResponseDto
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<ProductResponseDto>> findAllProducts(
            @Valid ProductSearchDto request,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Page<ProductResponseDto> result = productService.findAll(
                ProductSpecification.getSpecification(request, SecurityUtils.getUserFromJwtToken(jwt, authentication)),
                RequestUtils.getSortAndPageRequest(request)
        );
        return ResponseUtils.createPaginatedResponse(result);
    }

    /**
     * GET endpoint for retrieve list of popular products
     *
     * @return Response with status 200 OK and payload with collection of ProductResponseDto
     */

    @GetMapping("/popular")
    public ResponseEntity<PayloadResponseDto<ProductResponseDto>> getPopularProducts() {
        return ResponseUtils.createCollectionResponse(productService.findPopular());
    }

    /**
     * GET endpoint for retrieve available products by list of identifiers
     *
     * @param productIds List of Product identifiers
     * @return Response with status 200 OK and payload with collection of ProductResponseDto
     */

    @GetMapping("ids")
    public ResponseEntity<PayloadResponseDto<ProductResponseDto>> getProductsById(
            @RequestParam(name = "product_id") List<Integer> productIds
    ) {
        return ResponseUtils.createCollectionResponse(productService.findAvailableByIds(productIds));
    }

    /**
     * GET endpoint for retrieve products by Product identifiers
     *
     * @param ids Product identifiers
     * @return Response with status 200 OK and payload with collection of ProductResponseDto
     */

    @GetMapping("by_ids")
    public ResponseEntity<PayloadResponseDto<ProductResponseDto>> findProductsById(
            @RequestParam List<Integer> ids
    ) {
        List<ProductResponseDto> result = productService.findByIds(ids);
        return ResponseUtils.createCollectionResponse(result);
    }

    /**
     * POST endpoint for create Product entity
     *
     * @param request    DTO with create data
     * @param uriBuilder UriComponentsBuilder - autowired for create redirect
     * @return Response with status 201 Created and single payload with created Product entity
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<ProductResponseDto>> createProduct(
            @Valid @RequestBody ProductCreateDto request,
            UriComponentsBuilder uriBuilder
    ) {
        ProductResponseDto result = productService.create(request);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/v1/products/{productId}")
                        .build(Map.of("productId", result.getId())))
                .body(SinglePayloadResponseDto.<ProductResponseDto>builder()
                        .code(HttpStatus.CREATED.value())
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .payload(result)
                        .build());
    }

    /**
     * PATCH endpoint for batch patch Product entities
     *
     * @param request DTO with batch patch data
     * @return Response with status 204 No Content without payload
     */

    @PatchMapping
    public ResponseEntity<Void> updateProducts(
            @Valid @RequestBody ProductBatchPatchRequestDto request
    ) {
        productService.updateAll(request);
        return ResponseEntity.noContent().build();
    }
}
