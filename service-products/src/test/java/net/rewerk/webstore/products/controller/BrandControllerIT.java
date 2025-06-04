package net.rewerk.webstore.products.controller;

import jakarta.persistence.EntityNotFoundException;
import net.rewerk.webstore.dto.request.brand.BrandPatchDto;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Brand;
import net.rewerk.webstore.products.configuration.SecurityConfiguration;
import net.rewerk.webstore.products.dto.mapper.BrandDtoMapper;
import net.rewerk.webstore.products.service.entity.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = BrandController.class)
@Import(SecurityConfiguration.class)
public class BrandControllerIT {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private BrandService brandService;
    @MockitoBean
    private BrandDtoMapper brandDtoMapper;
    private Brand brand;
    BrandResponseDto brandResponseDto;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1);
        brand.setName("Test");
        brand.setImage("Test image");

        brandResponseDto = BrandResponseDto.builder()
                .id(1)
                .name("Test")
                .image("Test image")
                .build();
    }

    @Test
    public void givenBrand_whenGetBrand_thenReturnBrand() throws Exception {
        given(brandDtoMapper.toDto(brand)).willReturn(brandResponseDto);
        given(brandService.findById(1)).willReturn(brand);

        mvc.perform(get("/api/v1/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("admin").roles("ADMIN"))
                )
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(200),
                        jsonPath("$.message").value("OK"),
                        jsonPath("$.payload.id").value(1),
                        jsonPath("$.payload.name").value("Test"),
                        jsonPath("$.payload.image").value("Test image")
                );

        verify(brandService, times(1)).findById(1);
    }

    @Test
    public void givenInvalidBrandId_whenGetBrand_thenReturnNotFound() throws Exception {
        given(brandService.findById(1)).willThrow(new EntityNotFoundException("Brand not found"));

        mvc.perform(get("/api/v1/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("admin").roles("ADMIN")))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(404),
                        jsonPath("$.message").value("Not Found"),
                        jsonPath("$.details").value("Brand not found")
                );

        verify(brandService, times(1)).findById(1);
    }

    @Test
    public void givenBrand_whenGetCategories_thenReturnCategoriesList() throws Exception {
        List<CategoryResponseDto> categoryResponseDtoList = List.of(
                CategoryResponseDto.builder()
                        .id(1)
                        .name("Category 1")
                        .build(),
                CategoryResponseDto.builder()
                        .id(2)
                        .name("Category 2")
                        .build()
        );

        given(brandService.findById(1)).willReturn(brand);
        given(brandService.findCategoriesByBrand(brand)).willReturn(categoryResponseDtoList);

        mvc.perform(get("/api/v1/brands/1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(200),
                        jsonPath("$.message").value("OK"),
                        jsonPath("$.payload", hasSize(2)),
                        jsonPath("$.payload[0].id").value(1),
                        jsonPath("$.payload[0].name").value("Category 1"),
                        jsonPath("$.payload[1].id").value(2),
                        jsonPath("$.payload[1].name").value("Category 2")
                );
    }

    @Test
    public void givenValidPatchPayload_whenPatchBrand_thenReturnNoContent() throws Exception {
        BrandPatchDto patchDto = new BrandPatchDto();
        patchDto.setName("Test edited");
        patchDto.setImage("Test image edited");

        given(brandService.findById(1)).willReturn(brand);

        mvc.perform(patch("/api/v1/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {
                                    "name": "Test edited",
                                    "image": "Test image edited"
                                  }
                                """)
                        .with(user("admin").roles("ADMIN"))
                )
                .andExpectAll(
                        status().isNoContent()
                );

        verify(brandService, times(1)).findById(1);
        verify(brandService, times(1)).update(brand, patchDto);
    }

    @Test
    public void givenPatchPayload_whenNotAuthenticated_thenReturnError() throws Exception {
        BrandPatchDto patchDto = new BrandPatchDto();
        patchDto.setName("Test edited");
        patchDto.setImage("Test image edited");

        given(brandService.findById(1)).willReturn(brand);

        mvc.perform(patch("/api/v1/brands/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                          {
                            "name": "Test edited",
                            "image": "Test image edited"
                          }
                        """)
        ).andExpect(
                status().isUnauthorized()
        );

        verify(brandService, times(0)).findById(1);
        verify(brandService, times(0)).update(brand, patchDto);
    }

    @Test
    public void givenInvalidPatchPayload_whenPatchBrand_thenReturnValidationError() throws Exception {
        BrandPatchDto patchDto = new BrandPatchDto();
        patchDto.setName(" ");
        patchDto.setImage("");

        given(brandService.findById(1)).willReturn(brand);

        mvc.perform(patch("/api/v1/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .locale(Locale.of("en", "US"))
                        .with(user("admin").roles("ADMIN"))
                        .content("""
                                  {
                                    "name": " ",
                                    "image": ""
                                  }
                                """)
                )
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(400),
                        jsonPath("$.message").value("Bad Request"),
                        jsonPath("$.errors.length()")
                                .value(2),
                        jsonPath("$.errors.image")
                                .value("Image parameter can not be empty"),
                        jsonPath("$.errors.name")
                                .value("Name parameter length should be in range 2-256")
                );

        verify(brandService, times(1)).findById(1);
        verify(brandService, times(0)).update(brand, patchDto);
    }

    @Test
    public void givenCorrectBrandId_whenDeleteBrandNotAuthenticated_thenReturnNotAuthenticatedError()
            throws Exception {
        given(brandService.findById(1)).willReturn(brand);

        mvc.perform(delete("/api/v1/brands/1"))
                .andExpect(
                        status().isUnauthorized()
                );

        verify(brandService, times(0)).findById(1);
        verify(brandService, times(0)).delete(brand);
    }

    @Test
    public void givenCorrectBrandId_whenDeleteBrand_thenReturnNoContent() throws Exception {
        given(brandService.findById(1)).willReturn(brand);

        mvc.perform(delete("/api/v1/brands/1")
                        .with(user("admin").roles("ADMIN"))
                )
                .andDo(print())
                .andExpect(
                        status().isNoContent()
                );

        verify(brandService, times(1)).findById(1);
        verify(brandService, times(1)).delete(brand);
    }
}
