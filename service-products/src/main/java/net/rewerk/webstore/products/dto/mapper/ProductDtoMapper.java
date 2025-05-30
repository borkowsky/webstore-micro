package net.rewerk.webstore.products.dto.mapper;

import net.rewerk.webstore.dto.request.product.ProductCreateDto;
import net.rewerk.webstore.dto.request.product.ProductPatchDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductDtoMapper {
    Product createProductFromDto(ProductCreateDto productCreateDto);

    Product updateProduct(@MappingTarget Product product, ProductPatchDto productPatchDto);

    ProductResponseDto toDto(Product product);

    List<ProductResponseDto> toDto(List<Product> products);
}
