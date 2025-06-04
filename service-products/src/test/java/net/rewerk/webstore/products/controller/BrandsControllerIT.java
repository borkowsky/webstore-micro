package net.rewerk.webstore.products.controller;

import net.rewerk.webstore.dto.request.brand.BrandCreateDto;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.products.configuration.SecurityConfiguration;
import net.rewerk.webstore.products.dto.mapper.BrandDtoMapper;
import net.rewerk.webstore.products.dto.mapper.CategoryDtoMapper;
import net.rewerk.webstore.products.service.entity.BrandService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Import({
        SecurityConfiguration.class
})
@WebMvcTest(controllers = BrandsController.class)
public class BrandsControllerIT {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private BrandService brandService;
    @MockitoBean
    private BrandDtoMapper brandDtoMapper;
    @MockitoBean
    private CategoryDtoMapper categoryDtoMapper;
    private List<BrandResponseDto> brandsResponseList;

    @BeforeEach
    public void setup() {
        brandsResponseList = List.of(
                BrandResponseDto.builder()
                        .id(1)
                        .name("Brand 1")
                        .image("Brand image 1")
                        .build(),
                BrandResponseDto.builder()
                        .id(2)
                        .name("Brand 2")
                        .image("Brand image 2")
                        .build());
    }

    @Test
    public void whenFindAllBrands_thenReturnPageOfBrands() throws Exception {
        given(brandService.findAll(isA(Specification.class), isA(Pageable.class)))
                .willReturn(new PageImpl<>(brandsResponseList));

        mvc.perform(get("/api/v1/brands"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(200),
                        jsonPath("$.message").value("OK"),
                        jsonPath("$.payload", hasSize(2)),
                        jsonPath("$.payload[0].id").value(1),
                        jsonPath("$.payload[0].name").value("Brand 1"),
                        jsonPath("$.payload[0].image").value("Brand image 1")
                );

        verify(brandService, times(1))
                .findAll(isA(Specification.class), isA(Pageable.class));
    }

    @Test
    public void givenSearchDtoWithCategoryId_whenFindAllBrands_thenReturnPageOfBrands() throws Exception {
        given(brandService.findAllByProductCategoryId(5)).willReturn(
                new PageImpl<>(List.of(brandsResponseList.getFirst()))
        );

        mvc.perform(get("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("product_category_id", String.valueOf(5))
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(200),
                        jsonPath("$.message").value("OK"),
                        jsonPath("$.payload", hasSize(1)),
                        jsonPath("$.payload[0].id").value(1),
                        jsonPath("$.payload[0].name").value("Brand 1"),
                        jsonPath("$.payload[0].image").value("Brand image 1")
                );

        verify(brandService, times(1)).findAllByProductCategoryId(5);
    }

    @Test
    public void givenValidPayload_whenNotAuthenticated_thenReturnNotAuthenticated() throws Exception {
        BrandCreateDto createDto = new BrandCreateDto();
        createDto.setName("Brand created");
        createDto.setImage("Brand created image");

        given(brandService.create(createDto)).willReturn(BrandResponseDto.builder()
                .id(15)
                .name("Brand created")
                .image("Brand created image")
                .build());

        mvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {
                                      "name": "Brand created",
                                      "image": "Brand created image"
                                 }
                                """)
                )
                .andExpect(status().isUnauthorized());

        verify(brandService, times(0)).create(createDto);
    }

    @Test
    public void givenValidPayload_whenAuthenticatedAndRoleUser_thenReturnForbidden() throws Exception {
        BrandCreateDto createDto = new BrandCreateDto();
        createDto.setName("Brand created");
        createDto.setImage("Brand created image");

        given(brandService.create(createDto)).willReturn(BrandResponseDto.builder()
                .id(15)
                .name("Brand created")
                .image("Brand created image")
                .build());

        mvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("user").roles("USER"))
                        .content("""
                                  {
                                      "name": "Brand created",
                                      "image": "Brand created image"
                                 }
                                """)
                )
                .andExpect(status().isForbidden());

        verify(brandService, times(0)).create(createDto);
    }

    @Test
    public void givenValidPayload_whenAuthenticatedAndRoleAdmin_thenReturnCreated() throws Exception {
        BrandCreateDto createDto = new BrandCreateDto();
        createDto.setName("Brand created");
        createDto.setImage("Brand created image");

        given(brandService.create(createDto)).willReturn(BrandResponseDto.builder()
                .id(15)
                .name("Brand created")
                .image("Brand created image")
                .build());

        mvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("admin").roles("ADMIN"))
                        .content("""
                                  {
                                      "name": "Brand created",
                                      "image": "Brand created image"
                                 }
                                """)
                )
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value("201"),
                        jsonPath("$.message").value("Created"),
                        jsonPath("$.payload.id").value(15),
                        jsonPath("$.payload.name").value("Brand created"),
                        jsonPath("$.payload.image").value("Brand created image")
                );

        verify(brandService, times(1)).create(createDto);
    }

    @Test
    public void givenInvalidPayload_whenAuthenticatedAndRoleAdmin_thenReturnBadRequest() throws Exception {
        BrandCreateDto createDto = new BrandCreateDto();
        createDto.setName("");
        createDto.setImage("");

        mvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(Locale.of("en", "US"))
                        .with(user("admin").roles("ADMIN"))
                        .content("""
                                  {
                                      "name": "",
                                      "image": ""
                                 }
                                """)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(400),
                        jsonPath("$.message").value("Bad Request"),
                        jsonPath("$.errors.length()").value(2),
                        jsonPath("$.errors.image")
                                .value("Image parameter can not be empty"),
                        jsonPath("$.errors.name")
                                .value("Name parameter length should be in range 2-256")
                );

        verify(brandService, times(0)).create(createDto);
    }
}
