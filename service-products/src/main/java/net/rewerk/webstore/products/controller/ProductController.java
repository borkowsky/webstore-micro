package net.rewerk.webstore.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.product.ProductPatchDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Product;
import net.rewerk.webstore.products.dto.mapper.ProductDtoMapper;
import net.rewerk.webstore.products.service.entity.ProductService;
import net.rewerk.webstore.utility.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Parametrized REST controller for Product service
 *
 * @author rewerk
 */

@RestController
@RequestMapping("/api/v1/products/{id:\\d+}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductDtoMapper productDtoMapper;

    /**
     * Method for populate product method attribute by Product identifier request mapping path variable
     *
     * @param id Product identifier
     * @return Product entity
     */

    @ModelAttribute("product")
    public Product getProduct(@PathVariable Integer id
    ) {
        return productService.findById(id);
    }

    /**
     * GET endpoint for find Product entity by identifier
     *
     * @param product Product entity model attribute
     * @return Response with status 200 OK and single payload with ProductResponseDto
     */

    @GetMapping
    public ResponseEntity<SinglePayloadResponseDto<ProductResponseDto>> findProduct(
            @ModelAttribute("product") Product product
    ) {
        return ResponseUtils.createSingleResponse(productDtoMapper.toDto(product));
    }

    /**
     * PATCH endpoint for patch Product entity
     *
     * @param currentProduct Product entity model attribute
     * @param request        DTO with patch data
     * @return Response with status 204 No Content without payload
     */

    @PatchMapping
    public ResponseEntity<Void> updateProduct(
            @ModelAttribute("product") Product currentProduct,
            @Valid @RequestBody ProductPatchDto request
    ) {
        productService.update(currentProduct, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE endpoint for delete Product entity
     *
     * @param product Product entity model attribute
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(
            @ModelAttribute("product") Product product
    ) {
        productService.delete(product);
        return ResponseEntity.noContent().build();
    }
}
