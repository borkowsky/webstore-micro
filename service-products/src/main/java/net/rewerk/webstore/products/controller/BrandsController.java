package net.rewerk.webstore.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.brand.BrandCreateDto;
import net.rewerk.webstore.dto.request.brand.BrandSearchDto;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.products.service.entity.BrandService;
import net.rewerk.webstore.products.specification.BrandSpecification;
import net.rewerk.webstore.utility.RequestUtils;
import net.rewerk.webstore.utility.ResponseUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Not parametrized REST controller for Brand service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/brands")
public class BrandsController {
    private final BrandService brandService;

    /**
     * Method for find brands by search criteria
     *
     * @param request DTO with search parameters
     * @return Response with status 200 OK and paginated payload with page of Brand entity response DTO
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<BrandResponseDto>> findAllBrands(
            @Valid BrandSearchDto request
    ) {
        Page<BrandResponseDto> result = request.getProduct_category_id() != null ?
                brandService.findAllByProductCategoryId(request.getProduct_category_id())
                : brandService.findAll(
                BrandSpecification.getSpecification(request),
                RequestUtils.getSortAndPageRequest(request)
        );
        return ResponseUtils.createPaginatedResponse(result);
    }

    /**
     * Method for create Brand entity
     *
     * @param request    DTO with create data
     * @param uriBuilder UriComponentsBuilder - autowired for create redirect location
     * @return Response with status 201 Created and single payload of created Brand entity response DTO
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<BrandResponseDto>> createBrand(
            @Valid @RequestBody BrandCreateDto request,
            UriComponentsBuilder uriBuilder
    ) {
        BrandResponseDto result = brandService.create(request);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/v1/brands/{brandId}")
                        .build(Map.of("brandId", result.getId())))
                .body(SinglePayloadResponseDto.<BrandResponseDto>builder()
                        .code(HttpStatus.CREATED.value())
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .payload(result)
                        .build());
    }
}
