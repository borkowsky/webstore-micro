package net.rewerk.webstore.products.service.entity.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.entity.UploadDto;
import net.rewerk.webstore.dto.mq.uploads.UploadsDeleteObjectsDto;
import net.rewerk.webstore.dto.request.brand.BrandCreateDto;
import net.rewerk.webstore.dto.request.brand.BrandPatchDto;
import net.rewerk.webstore.dto.request.upload.UploadMultipleDeleteDto;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Brand;
import net.rewerk.webstore.products.dto.mapper.BrandDtoMapper;
import net.rewerk.webstore.products.dto.mapper.CategoryDtoMapper;
import net.rewerk.webstore.products.feign.client.UploadsFeignClient;
import net.rewerk.webstore.products.mq.Suppliers;
import net.rewerk.webstore.products.repository.BrandRepository;
import net.rewerk.webstore.products.service.EventWritingService;
import net.rewerk.webstore.products.service.entity.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Objects;

/**
 * Brand entity service implementation
 *
 * @author rewerk
 */

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BrandServiceImpl extends EventWritingService implements BrandService {
    private final BrandDtoMapper brandDtoMapper;
    private final CategoryDtoMapper categoryDtoMapper;
    private final BrandRepository brandRepository;
    private final UploadsFeignClient uploadsFeignClient;
    private final Suppliers mqSuppliers;

    /**
     * Find Brand entity by identifier
     *
     * @param id Brand identifier
     * @return Brand entity
     * @throws EntityNotFoundException Brand not found
     */

    @Override
    public Brand findById(Integer id) throws EntityNotFoundException {
        log.info("BrandServiceImpl.findById: Find brand by id {}", id);
        return brandRepository.findById(id).orElseThrow(() -> {
            log.error("BrandServiceImpl.findById: Brand with id {} not found", id);
            return new EntityNotFoundException("Brand not found");
        });
    }

    /**
     * Create Brand entity
     *
     * @param brand Brand entity to create
     * @return Created Brand entity response DTO
     * @throws EntityExistsException Brand with provided name already exists
     */

    @Override
    public BrandResponseDto create(@NonNull Brand brand) throws EntityExistsException {
        log.info("BrandServiceImpl.create: Create brand {}", brand);
        if (brandRepository.existsByName(brand.getName())) {
            log.error("BrandServiceImpl.create: Brand with name {} already exists", brand.getName());
            throw new EntityExistsException(
                    "Brand with name %s already exists".formatted(brand.getName())
            );
        }
        brand = brandRepository.save(brand);
        super.writeEvent(mqSuppliers, "Created brand: %s (ID %d)".formatted(brand.getName(), brand.getId()));
        return brandDtoMapper.toDto(brand);
    }

    /**
     * Create Brand entity
     *
     * @param dto DTO with create data
     * @return Created Brand entity response DTO
     */

    @Override
    public BrandResponseDto create(BrandCreateDto dto) {
        log.info("BrandServiceImpl.create: Create brand, DTO = {}", dto);
        Brand brand = brandDtoMapper.createDtoToBrand(dto);
        return this.create(brand);
    }

    /**
     * Update Brand entity
     *
     * @param brand Brand entity to update
     * @return Updated Brand entity
     */

    @Override
    public Brand update(Brand brand) {
        log.info("BrandServiceImpl.update: Update brand by entity {}", brand);
        brand = brandRepository.save(brand);
        super.writeEvent(mqSuppliers, "Updated brand: %s (ID %d)".formatted(brand.getName(), brand.getId()));
        return brand;
    }

    /**
     * Update Brand entity by DTO
     *
     * @param brand Brand entity to update
     * @param dto   DTO with patch data
     */

    @Override
    public void update(Brand brand, BrandPatchDto dto) {
        log.info("BrandServiceImpl.update: Update brand, entity = {}, DTO = {}", brand, dto);
        if (!Objects.equals(dto.getImage(), brand.getImage())) {
            uploadsFeignClient.deleteUploads(UploadMultipleDeleteDto.builder()
                    .filenames(List.of(brand.getImage()))
                    .type(UploadDto.Type.BRAND_IMAGE)
                    .build());
        }
        Brand result = brandDtoMapper.patchDtoToBrand(brand, dto);
        this.update(result);
    }

    /**
     * Delete Brand entity
     *
     * @param brand Brand entity to delete
     */

    @Override
    public void delete(Brand brand) {
        log.info("BrandServiceImpl.delete: Delete brand, entity = {}", brand);
        if (brand.getImage() != null) {
            log.info("BrandServiceImpl.delete: send message to delete upload object SCS queue, image = {}",
                    brand.getImage());
            mqSuppliers.getUploadsDeleteObjectSink().emitNext(MessageBuilder
                    .withPayload(
                            UploadsDeleteObjectsDto.builder()
                                    .object_names(List.of(brand.getImage()))
                                    .type(UploadDto.Type.BRAND_IMAGE)
                                    .build()
                    ).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        }
        brandRepository.delete(brand);
        super.writeEvent(mqSuppliers, "Deleted brand: %s (ID %d)".formatted(brand.getName(), brand.getId()));
    }

    /**
     * Find page of Brand entities by pageable
     *
     * @param pageable Spring Pageable object
     * @return Page of Brand entity response DTO
     */

    @Override
    public Page<BrandResponseDto> findAll(Pageable pageable) {
        log.info("BrandServiceImpl.findAll: Find page of all brands, pageable = {}", pageable);
        Page<Brand> page = brandRepository.findAll(pageable);
        return new PageImpl<>(brandDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Find page of Brand entities by specification and pageable
     *
     * @param specification Brand entity JPA specification
     * @param pageable      Spring Pageable object
     * @return Page of Brand entity response DTO
     */

    @Override
    public Page<BrandResponseDto> findAll(Specification<Brand> specification, Pageable pageable) {
        log.info("BrandServiceImpl.findAll: Find page of brands");
        Page<Brand> page = brandRepository.findAll(specification, pageable);
        return new PageImpl<>(brandDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Find page of Brand entities by Product category identifier
     *
     * @param productCategoryId Product Category identifier
     * @return Page of Brand entity response DTO
     */

    @Override
    public Page<BrandResponseDto> findAllByProductCategoryId(Integer productCategoryId) {
        log.info("BrandServiceImpl.findAllByProductCategoryId: Find page of brands, productCategoryId = {}",
                productCategoryId);
        List<Brand> result = brandRepository.findDistinctByProductCategoryId(productCategoryId);
        return new PageImpl<>(
                brandDtoMapper.toDto(result),
                PageRequest.of(0, result.size() + 1),
                result.size()
        );
    }

    /**
     * Search Brand entities by name
     *
     * @param name     Brand name
     * @param pageable Spring Pageable object
     * @return Page of Brand entity response DTO
     */

    @Override
    public Page<BrandResponseDto> search(String name, Pageable pageable) {
        log.info("BrandServiceImpl.search: Find page of brands by name, name = {}", name);
        Page<Brand> page = brandRepository.findAllByNameLikeIgnoreCase(name, pageable);
        return new PageImpl<>(brandDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Find list of Category entity by Brand identifier
     *
     * @param brand Brand identifier
     * @return List of Category entity response DTO
     */

    @Override
    public List<CategoryResponseDto> findCategoriesByBrand(Brand brand) {
        log.info("BrandServiceImpl.findCategoriesByBrand: Find page of categories by brand = {}", brand);
        return categoryDtoMapper.toDto(brandRepository.findDistinctCategoriesByProductBrandId(brand.getId()));
    }
}
