package net.rewerk.webstore.products.dto.mapper;

import net.rewerk.webstore.dto.request.brand.BrandCreateDto;
import net.rewerk.webstore.dto.request.brand.BrandPatchDto;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BrandDtoMapper {
    Brand createDtoToBrand(BrandCreateDto dto);

    Brand patchDtoToBrand(@MappingTarget Brand brand, BrandPatchDto dto);

    BrandResponseDto toDto(Brand brand);

    List<BrandResponseDto> toDto(List<Brand> brands);
}
