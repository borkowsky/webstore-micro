package net.rewerk.webstore.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.brand.BrandPatchDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Brand;
import net.rewerk.webstore.products.dto.mapper.BrandDtoMapper;
import net.rewerk.webstore.products.service.entity.BrandService;
import net.rewerk.webstore.utility.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Parametrized REST controller for Brand service
 *
 * @author rewerk
 */

@RequestMapping("/api/v1/brands/{id:\\d+}")
@RestController
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;
    private final BrandDtoMapper brandDtoMapper;

    /**
     * Method for populate brand method attribute by Brand identifier request mapping path variable
     *
     * @param id Brand identifier
     * @return Brand entity
     */

    @ModelAttribute("brand")
    public Brand getBrand(@PathVariable Integer id) {
        return brandService.findById(id);
    }

    /**
     * Method for find Brand by identifier
     *
     * @param brand Brand entity model attribute
     * @return Response with 200 OK status and single payload of BrandResponseDto
     */

    @GetMapping
    public ResponseEntity<SinglePayloadResponseDto<BrandResponseDto>> findBrand(
            @ModelAttribute("brand") Brand brand
    ) {
        return ResponseUtils.createSingleResponse(brandDtoMapper.toDto(brand));
    }

    /**
     * Method for find categories by Brand
     *
     * @param brand Brand entity model attribute
     * @return ResponseEntity with status 200 OK and response with collection of CategoryResponseDto
     */

    @GetMapping("/categories")
    public ResponseEntity<PayloadResponseDto<CategoryResponseDto>> findCategoriesByBrand(
            @ModelAttribute("brand") Brand brand
    ) {
        return ResponseUtils.createCollectionResponse(brandService.findCategoriesByBrand(brand));
    }

    /**
     * Method for update Brand entity
     *
     * @param brand   Brand entity model attribute
     * @param request DTO with patch data
     * @return Response entity with status 204 No Content without payload
     */

    @PatchMapping
    public ResponseEntity<Void> updateBrand(
            @ModelAttribute("brand") Brand brand,
            @Valid @RequestBody BrandPatchDto request
    ) {
        brandService.update(brand, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Method for delete Brand entity
     *
     * @param brand Brand entity model attribute
     * @return Response entity with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteBrand(
            @ModelAttribute("brand") Brand brand
    ) {
        brandService.delete(brand);
        return ResponseEntity.noContent().build();
    }
}
