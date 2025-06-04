package net.rewerk.webstore.products.controller;

import net.rewerk.webstore.dto.request.category.CategoryCreateDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.products.configuration.SecurityConfiguration;
import net.rewerk.webstore.products.service.entity.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(CategoriesController.class)
@Import({
        SecurityConfiguration.class
})
public class CategoriesControllerIT {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private CategoryService categoryService;
    List<CategoryResponseDto> categoriesList;

    @BeforeEach
    void setup() {
        categoriesList = List.of(
                CategoryResponseDto.builder()
                        .id(1)
                        .name("Category 1")
                        .icon("Category icon 1")
                        .categories(List.of())
                        .build(),
                CategoryResponseDto.builder()
                        .id(2)
                        .name("Category 2")
                        .icon("Category icon 2")
                        .categories(List.of(
                                CategoryResponseDto.builder()
                                        .id(3)
                                        .name("Subcategory 3")
                                        .icon("Subcategory icon 3")
                                        .categories(List.of())
                                        .categoryId(2)
                                        .build()
                        ))
                        .build());
    }

    @Test
    public void whenFindAllCategories_thenReturnPageOfCategories() throws Exception {
        given(categoryService.findAll(isA(Specification.class), isA(Pageable.class)))
                .willReturn(new PageImpl<>(categoriesList));

        mvc.perform(get("/api/v1/categories"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(200),
                        jsonPath("$.message").value("OK"),
                        jsonPath("$.payload.length()").value(2),
                        jsonPath("$.payload[0].id").value(1),
                        jsonPath("$.payload[0].name").value("Category 1"),
                        jsonPath("$.payload[0].icon").value("Category icon 1"),
                        jsonPath("$.payload[0].categories.length()").value(0),
                        jsonPath("$.payload[0].categoryId").doesNotExist(),
                        jsonPath("$.payload[1].id").value(2),
                        jsonPath("$.payload[1].name").value("Category 2"),
                        jsonPath("$.payload[1].icon").value("Category icon 2"),
                        jsonPath("$.payload[1].categories.length()").value(1),
                        jsonPath("$.payload[1].categoryId").doesNotExist(),
                        jsonPath("$.payload[1].categories[0].id").value(3),
                        jsonPath("$.payload[1].categories[0].name").value("Subcategory 3"),
                        jsonPath("$.payload[1].categories[0].icon").value("Subcategory icon 3"),
                        jsonPath("$.payload[1].categories[0].categories.length()").value(0),
                        jsonPath("$.payload[1].categories[0].categoryId").value(2)
                );

        verify(categoryService, times(1))
                .findAll(isA(Specification.class), isA(Pageable.class));
    }

    @Test
    public void whenFindCategoriesByProductIds_thenReturnListOfCategories() throws Exception {
        List<Integer> productIds = List.of(1, 2, 3);

        given(categoryService.findAllDistinctByProductIdIn(productIds))
                .willReturn(categoriesList);

        mvc.perform(get("/api/v1/categories/by_products")
                        .param("productIds", productIds.getFirst().toString())
                        .param("productIds", productIds.get(1).toString())
                        .param("productIds", productIds.get(2).toString())
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(200),
                        jsonPath("$.message").value("OK"),
                        jsonPath("$.payload.length()").value(2),
                        jsonPath("$.payload[0].id").value(1),
                        jsonPath("$.payload[0].name").value("Category 1"),
                        jsonPath("$.payload[0].icon").value("Category icon 1"),
                        jsonPath("$.payload[0].categories.length()").value(0),
                        jsonPath("$.payload[0].categoryId").doesNotExist(),
                        jsonPath("$.payload[1].id").value(2),
                        jsonPath("$.payload[1].name").value("Category 2"),
                        jsonPath("$.payload[1].icon").value("Category icon 2"),
                        jsonPath("$.payload[1].categories.length()").value(1),
                        jsonPath("$.payload[1].categoryId").doesNotExist(),
                        jsonPath("$.payload[1].categories[0].id").value(3),
                        jsonPath("$.payload[1].categories[0].name").value("Subcategory 3"),
                        jsonPath("$.payload[1].categories[0].icon").value("Subcategory icon 3"),
                        jsonPath("$.payload[1].categories[0].categories.length()").value(0),
                        jsonPath("$.payload[1].categories[0].categoryId").value(2)
                );

        verify(categoryService, times(1)).findAllDistinctByProductIdIn(productIds);
    }

    @Test
    public void givenValidPayload_whenNotAuthenticated_thenReturnNotAuthenticated() throws Exception {
        CategoryCreateDto createDto = new CategoryCreateDto();
        createDto.setName("Category created");
        createDto.setIcon("Category icon created");
        createDto.setCategory_id(1);

        given(categoryService.create(createDto)).willReturn(
                CategoryResponseDto.builder()
                        .id(4)
                        .name("Category created")
                        .icon("Category icon created")
                        .categories(List.of())
                        .categoryId(1)
                        .build()
        );

        mvc.perform(post("/api/v1/categories"))
                .andExpect(status().isUnauthorized());

        verify(categoryService, times(0)).create(createDto);
    }

    @Test
    public void givenValidPayload_whenAuthenticatedRoleUser_thenReturnForbidden() throws Exception {
        CategoryCreateDto createDto = new CategoryCreateDto();
        createDto.setName("Category created");
        createDto.setIcon("Category icon created");
        createDto.setCategory_id(1);

        given(categoryService.create(createDto)).willReturn(
                CategoryResponseDto.builder()
                        .id(4)
                        .name("Category created")
                        .icon("Category icon created")
                        .categories(List.of())
                        .categoryId(1)
                        .build()
        );

        mvc.perform(post("/api/v1/categories")
                        .with(user("user").roles("USER"))
                )
                .andExpect(status().isForbidden());

        verify(categoryService, times(0)).create(createDto);
    }

    @Test
    public void givenValidPayload_whenAuthenticatedRoleAdmin_thenReturnCreated() throws Exception {
        CategoryCreateDto createDto = new CategoryCreateDto();
        createDto.setName("Category created");
        createDto.setIcon("Category icon created");
        createDto.setDescription("Category created description");
        createDto.setCategory_id(1);

        given(categoryService.create(eq(createDto))).willReturn(
                CategoryResponseDto.builder()
                        .id(4)
                        .name("Category created")
                        .description("Category created description")
                        .icon("Category icon created")
                        .categories(List.of())
                        .categoryId(1)
                        .productsCount(0)
                        .enabled(true)
                        .build()
        );

        mvc.perform(post("/api/v1/categories")
                        .with(user("user").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Category created",
                                    "icon": "Category icon created",
                                    "description": "Category created description",
                                    "category_id": 1
                                }
                                """)
                )
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        header()
                                .string(HttpHeaders.LOCATION, "http://localhost/api/v1/categories/4"),
                        jsonPath("$.code").value(201),
                        jsonPath("$.message").value("Created"),
                        jsonPath("$.payload.id").value(4),
                        jsonPath("$.payload.name")
                                .value("Category created"),
                        jsonPath("$.payload.description")
                                .value("Category created description"),
                        jsonPath("$.payload.icon")
                                .value("Category icon created"),
                        jsonPath("$.payload.categoryId").value(1),
                        jsonPath("$.payload.categories.length()").value(0),
                        jsonPath("$.payload.enabled").value(true)
                );

        verify(categoryService, times(1)).create(createDto);
    }

    @Test
    public void givenInvalidPayload_whenAuthenticatedRoleAdmin_thenReturnBadRequest() throws Exception {
        mvc.perform(post("/api/v1/categories")
                        .with(user("user").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(Locale.ENGLISH)
                        .content("""
                                {
                                 "name": "",
                                 "icon": ""
                                }
                                """)
                ).andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(400),
                        jsonPath("$.message").value("Bad Request"),
                        jsonPath("$.errors.length()").value(2),
                        jsonPath("$.errors.name")
                                .value("Name parameter length should be in range 4-64"),
                        jsonPath("$.errors.icon")
                                .value("Icon parameter required")
                );

        verify(categoryService, times(0)).create(any(CategoryCreateDto.class));
    }
}
