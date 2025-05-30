package net.rewerk.webstore.products.service.entity.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.request.category.CategoryCreateDto;
import net.rewerk.webstore.dto.request.category.CategoryPatchDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Category;
import net.rewerk.webstore.entity.Product;
import net.rewerk.webstore.entity.User;
import net.rewerk.webstore.products.dto.mapper.CategoryDtoMapper;
import net.rewerk.webstore.products.mq.Suppliers;
import net.rewerk.webstore.products.repository.CategoryRepository;
import net.rewerk.webstore.products.repository.ProductRepository;
import net.rewerk.webstore.products.service.EventWritingService;
import net.rewerk.webstore.products.service.entity.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Category entity service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class CategoryServiceImpl extends EventWritingService implements CategoryService {
    private final CategoryDtoMapper categoryDtoMapper;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final Suppliers mqSuppliers;

    /**
     * Find Category entity by identifier
     *
     * @param id Category identifier
     * @return Category entity
     * @throws EntityNotFoundException Category entity not found
     */

    @Override
    public Category findById(Integer id) throws EntityNotFoundException {
        log.info("CategoryServiceImpl.findById: Find category by id: {}", id);
        return categoryRepository.findById(id).orElseThrow(
                () -> {
                    log.error("CategoryServiceImpl.findById: Could not find category by id: {}", id);
                    return new EntityNotFoundException("Category not found");
                }
        );
    }

    /**
     * Find Category entity by identifier with access checks
     *
     * @param id   Category entity identifier
     * @param user Authenticated user
     * @return Category entity
     * @throws EntityNotFoundException Category entity not found
     */

    @Override
    public Category findById(Integer id, User user) throws EntityNotFoundException {
        log.info("CategoryServiceImpl.findById: access checked find category by id: {}", id);
        Category category = this.findById(id);
        if ((user == null || !User.Role.ROLE_ADMIN.equals(user.getRole())) && !category.getEnabled()) {
            log.error("CategoryServiceImpl.findById: Category with id {} was not found", id);
            throw new EntityNotFoundException("Category not found");
        }
        return category;
    }

    /**
     * Create Category entity
     *
     * @param dto DTO with create data
     * @return Created Category entity response DTO
     * @throws EntityNotFoundException Parent Category entity not found
     */

    @Override
    public CategoryResponseDto create(CategoryCreateDto dto) throws EntityNotFoundException {
        log.info("CategoryServiceImpl.create: Create category: {}", dto);
        Category mapped = categoryDtoMapper.createDtoToCategory(dto);
        if (dto.getCategory_id() != null) {
            categoryRepository.findById(dto.getCategory_id())
                    .ifPresentOrElse(
                            category -> mapped.setCategoryId(category.getId()),
                            () -> {
                                log.error("CategoryServiceImpl.create: Parent category with id {} was not found",
                                        dto.getCategory_id());
                                throw new EntityNotFoundException("Parent category was not found");
                            }
                    );
        }
        Category result = categoryRepository.save(mapped);
        super.writeEvent(mqSuppliers, "Created category: %s (ID %d)".formatted(result.getName(), result.getId()));
        return categoryDtoMapper.toDto(result);
    }

    /**
     * Update Category entity
     *
     * @param category Category entity to update
     * @param dto      DTO with update data
     * @throws EntityNotFoundException Parent Category entity not found
     */

    @Override
    public void update(Category category, CategoryPatchDto dto) throws EntityNotFoundException {
        log.info("CategoryServiceImpl.update: Update category: id = {}, dto = {}", category.getId(), dto);
        Category mapped = categoryDtoMapper.updateDtoToCategory(category, dto);
        if (dto.getEnabled() != null) {
            mapped.getCategories().forEach(c -> c.setEnabled(dto.getEnabled()));
        }
        if (dto.getCategory_id() != null) {
            categoryRepository.findById(dto.getCategory_id())
                    .ifPresentOrElse(
                            (parent) -> mapped.setCategoryId(parent.getId()),
                            () -> {
                                log.error("CategoryServiceImpl.update: parent category with id {} was not found",
                                        dto.getCategory_id());
                                throw new EntityNotFoundException("Parent category was not found");
                            });
        }
        Category result = categoryRepository.save(mapped);
        super.writeEvent(mqSuppliers, "Updated category: %s (ID %d)".formatted(result.getName(), result.getId()));
    }

    /**
     * Delete Category entity
     *
     * @param category Category entity to delete
     */

    @Override
    public void delete(Category category) {
        log.info("CategoryServiceImpl.delete: Delete category: {}", category);
        categoryRepository.deleteById(category.getId());
        super.writeEvent(mqSuppliers, "Deleted category: %s (ID %d)".formatted(category.getName(), category.getId()));
    }

    /**
     * Find Category entities by pageable request
     *
     * @param pageable Spring Pageable object
     * @return Page of Category entity response DTO
     */

    @Override
    public Page<CategoryResponseDto> findAll(Pageable pageable) {
        log.info("CategoryServiceImpl.findAll: Find all categories by pageable request = {}", pageable);
        Page<Category> page = categoryRepository.findAll(pageable);
        return new PageImpl<>(categoryDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Find Category entities by Category JPA Specification pageable request
     *
     * @param specification Category entity JPA specification
     * @param pageable      Spring Pageable object
     * @return Page of Category entity response DTO
     */

    @Override
    public Page<CategoryResponseDto> findAll(Specification<Category> specification, Pageable pageable) {
        log.info("CategoryServiceImpl.findAll: Find all categories by specification and pageable = {}",
                pageable);
        Page<Category> page = categoryRepository.findAll(specification, pageable);
        return new PageImpl<>(categoryDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Find distinct list of Category entities by list of Product identifiers
     *
     * @param productIds List of Product identifiers
     * @return List of Category entity response DTO
     */

    @Override
    public List<CategoryResponseDto> findAllDistinctByProductIdIn(List<Integer> productIds) {
        log.info("CategoryServiceImpl.findAllDistinctByProductIdIn: Find all categories by productIds = {}", productIds);
        List<Product> products = productRepository.findAllById(productIds);
        List<Category> categories = categoryRepository.findDistinctByIdIn(products.stream()
                .map(product -> product
                        .getCategory()
                        .getId()
                ).toList());
        return categoryDtoMapper.toDto(categories);
    }

    /**
     * Search Category entity by name and pageable request
     *
     * @param name     Category name search query
     * @param pageable Spring Pageable object
     * @return Page of Category entity response DTO
     */

    @Override
    public Page<CategoryResponseDto> search(String name, Pageable pageable) {
        log.info("CategoryServiceImpl.search: Find all categories by name = {} and pageable = {}", name, pageable);
        Page<Category> page = categoryRepository
                .findAllByNameLikeIgnoreCaseAndEnabled(name, true, pageable);
        return new PageImpl<>(categoryDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }
}
