package net.rewerk.webstore.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.category.CategoryPatchDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Category;
import net.rewerk.webstore.products.dto.mapper.CategoryDtoMapper;
import net.rewerk.webstore.products.service.entity.CategoryService;
import net.rewerk.webstore.utility.ResponseUtils;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Parametrized REST controller for Category service
 *
 * @author rewerk
 */

@RequestMapping("/api/v1/categories/{id:\\d+}")
@RequiredArgsConstructor
@RestController
class CategoryController {
    private final CategoryService categoryService;
    private final CategoryDtoMapper categoryDtoMapper;

    /**
     * Method for populate category method attribute by Category identifier request mapping path variable
     *
     * @param id Category identifier
     * @return Category entity
     */

    @ModelAttribute("category")
    public Category getCategory(@PathVariable Integer id,
                                @AuthenticationPrincipal Jwt jwt,
                                Authentication authentication) {
        return categoryService.findById(id, SecurityUtils.getUserFromJwtToken(jwt, authentication));
    }

    /**
     * GET endpoint for find Category entity by identifier
     *
     * @param category Category entity model attribute
     * @return Response with status 200 OK and single payload with CategoryResponseDto
     */

    @GetMapping
    public ResponseEntity<SinglePayloadResponseDto<CategoryResponseDto>> findCategory(
            @ModelAttribute("category") Category category
    ) {
        return ResponseUtils.createSingleResponse(categoryDtoMapper.toDto(category));
    }

    /**
     * PATCH endpoint for patch Category entity
     *
     * @param currentCategory Category entity model attribute
     * @param requestDto      DTO with patch data
     * @return Response with status 204 No Content without payload
     */

    @PatchMapping
    public ResponseEntity<Void> updateCategory(
            @ModelAttribute("category") Category currentCategory,
            @Valid @RequestBody CategoryPatchDto requestDto
    ) {
        categoryService.update(currentCategory, requestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE endpoint for delete Category entity
     *
     * @param category Category entity model attribute
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteCategory(
            @ModelAttribute("category") Category category
    ) {
        categoryService.delete(category);
        return ResponseEntity.noContent().build();
    }
}
