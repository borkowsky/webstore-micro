package net.rewerk.webstore.products.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.request.search.SearchRequestDto;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.dto.response.search.SearchResponseDto;
import net.rewerk.webstore.products.service.entity.BrandService;
import net.rewerk.webstore.products.service.entity.CategoryService;
import net.rewerk.webstore.products.service.entity.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class SearchService {
    private final CategoryService categoryService;
    private final ProductService productService;
    private final BrandService brandService;
    @Value("${search.max_entity_type_result}")
    private Integer MAX_ENTITY_TYPE_RESULT;

    public SearchResponseDto search(SearchRequestDto searchRequestDto) {
        log.info("SearchService.search: Search request: {}", searchRequestDto);
        String query = "%" + searchRequestDto.getQuery() + "%";
        Page<CategoryResponseDto> categories = categoryService.search(
                query, PageRequest.of(0, MAX_ENTITY_TYPE_RESULT)
        );
        Page<ProductResponseDto> products = productService.search(
                query, PageRequest.of(0, MAX_ENTITY_TYPE_RESULT)
        );
        Page<BrandResponseDto> brands = brandService.search(
                query, PageRequest.of(0, MAX_ENTITY_TYPE_RESULT)
        );
        return SearchResponseDto.builder()
                .categories(categories.getContent())
                .products(products.getContent())
                .brands(brands.getContent())
                .total(categories.getTotalElements() + products.getTotalElements() + brands.getTotalElements())
                .build();
    }
}
