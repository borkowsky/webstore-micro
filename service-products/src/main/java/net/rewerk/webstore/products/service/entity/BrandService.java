package net.rewerk.webstore.products.service.entity;

import net.rewerk.webstore.dto.request.brand.BrandCreateDto;
import net.rewerk.webstore.dto.request.brand.BrandPatchDto;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BrandService {
    Page<BrandResponseDto> findAll(Pageable pageable);

    Page<BrandResponseDto> findAll(Specification<Brand> specification, Pageable pageable);

    Page<BrandResponseDto> findAllByProductCategoryId(Integer productCategoryId);

    Page<BrandResponseDto> search(String name, Pageable pageable);

    List<CategoryResponseDto> findCategoriesByBrand(Brand brand);

    Brand findById(Integer id);

    BrandResponseDto create(Brand brand);

    BrandResponseDto create(BrandCreateDto dto);

    Brand update(Brand brand);

    void update(Brand brand, BrandPatchDto dto);

    void delete(Brand brand);
}
