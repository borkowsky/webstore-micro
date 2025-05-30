package net.rewerk.webstore.products.dto.mapper;

import net.rewerk.webstore.dto.request.category.CategoryCreateDto;
import net.rewerk.webstore.dto.request.category.CategoryPatchDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryDtoMapper {
    Category createDtoToCategory(CategoryCreateDto dto);

    Category updateDtoToCategory(@MappingTarget Category category, CategoryPatchDto categoryPatchDto);

    CategoryResponseDto toDto(Category category);

    List<CategoryResponseDto> toDto(List<Category> categories);
}
