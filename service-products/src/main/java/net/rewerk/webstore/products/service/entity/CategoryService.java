package net.rewerk.webstore.products.service.entity;

import net.rewerk.webstore.dto.request.category.CategoryCreateDto;
import net.rewerk.webstore.dto.request.category.CategoryPatchDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Category;
import net.rewerk.webstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CategoryService {
    Category findById(Integer id);

    Category findById(Integer id, User user);

    CategoryResponseDto create(CategoryCreateDto dto);

    void update(Category category, CategoryPatchDto dto);

    void delete(Category category);

    Page<CategoryResponseDto> findAll(Pageable pageable);

    Page<CategoryResponseDto> findAll(Specification<Category> specification, Pageable pageable);

    List<CategoryResponseDto> findAllDistinctByProductIdIn(List<Integer> productIds);

    Page<CategoryResponseDto> search(String name, Pageable pageable);
}
