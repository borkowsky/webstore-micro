package net.rewerk.webstore.dto.response.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {
    private List<CategoryResponseDto> categories;
    private List<ProductResponseDto> products;
    private List<BrandResponseDto> brands;
    private Long total;
}
