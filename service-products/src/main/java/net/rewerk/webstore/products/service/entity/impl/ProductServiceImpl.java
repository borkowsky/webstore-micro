package net.rewerk.webstore.products.service.entity.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.entity.UploadDto;
import net.rewerk.webstore.dto.mq.uploads.UploadsDeleteObjectsDto;
import net.rewerk.webstore.dto.request.product.ProductBatchPatchRequestDto;
import net.rewerk.webstore.dto.request.product.ProductCreateDto;
import net.rewerk.webstore.dto.request.product.ProductPatchDto;
import net.rewerk.webstore.dto.request.product.ProductUpdateRatingDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductActualRatingResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Brand;
import net.rewerk.webstore.entity.Category;
import net.rewerk.webstore.entity.Product;
import net.rewerk.webstore.products.dto.mapper.ProductDtoMapper;
import net.rewerk.webstore.products.feign.client.ReviewsFeignClient;
import net.rewerk.webstore.products.mq.Suppliers;
import net.rewerk.webstore.products.repository.ProductRepository;
import net.rewerk.webstore.products.service.EventWritingService;
import net.rewerk.webstore.products.service.entity.BrandService;
import net.rewerk.webstore.products.service.entity.CategoryService;
import net.rewerk.webstore.products.service.entity.ProductService;
import net.rewerk.webstore.utility.CommonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.util.Arrays;
import java.util.List;

/**
 * Product entity service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ProductServiceImpl extends EventWritingService implements ProductService {
    private final ProductDtoMapper productDtoMapper;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ReviewsFeignClient reviewsFeignClient;
    private final Suppliers mqSuppliers;

    /**
     * Find Product entity by identifier
     *
     * @param id Product identifier
     * @return Product entity
     * @throws EntityNotFoundException Product entity not found
     */

    @Override
    public Product findById(Integer id) throws EntityNotFoundException {
        log.info("ProductServiceImpl.findById: find product by id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ProductServiceImpl.findById: product with id {} was not found", id);
                    return new EntityNotFoundException("Product not found");
                });
    }

    /**
     * Find products by Spring Pageable request
     *
     * @param pageable Spring Pageable object
     * @return Page of Product entity response DTO
     */

    @Override
    public Page<ProductResponseDto> findAll(Pageable pageable) {
        log.info("ProductServiceImpl.findAll: find all products by pageable = {}", pageable);
        Page<Product> page = productRepository.findAll(pageable);
        return new PageImpl<>(productDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Find products by Product entity JPA specification and Spring Pageable request
     *
     * @param specification Product entity JPA specification
     * @param pageable      Spring Pageable object
     * @return Page of Product entity response DTO
     */

    @Override
    public Page<ProductResponseDto> findAll(Specification<Product> specification, Pageable pageable) {
        log.info("ProductServiceImpl.findAll: find all products by specification and pageable = {}",
                pageable);
        Page<Product> page = productRepository.findAll(specification, pageable);
        return new PageImpl<>(productDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Search products by name and Spring Pageable request
     *
     * @param name     Product name query
     * @param pageable Spring Pageable object
     * @return Page of Product entity response DTO
     */

    @Override
    public Page<ProductResponseDto> search(String name, Pageable pageable) {
        log.info("ProductServiceImpl.search: find all products by name {} and pageable = {}", name, pageable);
        Page<Product> page = productRepository
                .findAllByNameLikeIgnoreCaseAndEnabledAndBalanceGreaterThanAndCategoryEnabled(
                        name,
                        true,
                        0,
                        true,
                        pageable);
        return new PageImpl<>(productDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Find popular products
     *
     * @return List of Product entity response DTO
     */

    @Override
    public List<ProductResponseDto> findPopular() {
        log.info("ProductServiceImpl.findPopular: find all popular products");
        return productDtoMapper.toDto(productRepository.findTopPopularProducts());
    }

    /**
     * Find products by list of identifiers
     *
     * @param ids List of Product identifiers
     * @return List of Product entity response DTO
     */

    @Override
    public List<ProductResponseDto> findByIds(List<Integer> ids) {
        log.info("ProductServiceImpl.findByIds: find all products by ids {}", ids);
        return productDtoMapper.toDto(productRepository.findAllById(ids));
    }

    /**
     * Find available products by identifiers
     *
     * @param ids List of Product identifiers
     * @return List of Product entity response DTO
     */

    @Override
    public List<ProductResponseDto> findAvailableByIds(List<Integer> ids) {
        log.info("ProductServiceImpl.findAvailableByIds: find all available products by ids {}", ids);
        return productDtoMapper.toDto(
                productRepository
                        .findAllByIdInAndCategoryEnabledAndBalanceGreaterThanAndEnabled(
                                ids,
                                true,
                                0,
                                true
                        )
        );
    }

    /**
     * Create Product entity
     *
     * @param productCreateDto DTO with create data
     * @return Product entity response DTO
     */

    @Override
    public ProductResponseDto create(ProductCreateDto productCreateDto) {
        log.info("ProductServiceImpl.create: create product with productCreateDto = {}", productCreateDto);
        Product product = productDtoMapper.createProductFromDto(productCreateDto);
        Category category = categoryService.findById(productCreateDto.getCategory_id());
        Brand brand = brandService.findById(productCreateDto.getBrand_id());
        product.setCategory(category);
        product.setBrand(brand);
        product = productRepository.save(product);
        super.writeEvent(mqSuppliers, "Created product: %s (ID %d)".formatted(product.getName(), product.getId()));
        return productDtoMapper.toDto(product);
    }

    /**
     * Update Product entity
     *
     * @param product         Product entity to update
     * @param productPatchDto DTO with update data
     */

    @Override
    public void update(Product product, ProductPatchDto productPatchDto) {
        log.info("ProductServiceImpl.update: update product = {} with productPatchDto = {}", product, productPatchDto);
        Product mapped = productDtoMapper.updateProduct(product, productPatchDto);
        if (productPatchDto.getBrand_id() != null) {
            mapped.setBrand(brandService.findById(productPatchDto.getBrand_id()));
        }
        if (productPatchDto.getImages() != null) {
            List<String> imagesToDelete = CommonUtils.lostItemsInList(
                    product.getImages(), Arrays.asList(productPatchDto.getImages())
            );
            if (!imagesToDelete.isEmpty()) {
                mqSuppliers.getUploadsDeleteObjectSink().emitNext(MessageBuilder
                        .withPayload(
                                UploadsDeleteObjectsDto.builder()
                                        .object_names(imagesToDelete)
                                        .type(UploadDto.Type.PRODUCT_IMAGE)
                                        .build()
                        ).build(), Sinks.EmitFailureHandler.FAIL_FAST);
            }
        }
        if (productPatchDto.getCategory_id() != null) {
            product.setCategory(categoryService.findById(productPatchDto.getCategory_id()));
        }
        productRepository.save(product);
        super.writeEvent(mqSuppliers, "Updated product: %s (ID %d)".formatted(product.getName(), product.getId()));
    }

    /**
     * Batch update Product entities
     *
     * @param productBatchPatchRequestDto DTO with batch update data
     */

    @Override
    public void updateAll(ProductBatchPatchRequestDto productBatchPatchRequestDto) {
        log.info("ProductServiceImpl.updateAll: update all products with productBatchPatchRequestDto = {}",
                productBatchPatchRequestDto);
        List<Product> products = productRepository.findAllById(productBatchPatchRequestDto.getData().keySet());
        products = products.stream()
                .map(product -> productDtoMapper.updateProduct(
                                product,
                                productBatchPatchRequestDto.getData().get(product.getId())
                        )
                ).toList();
        productRepository.saveAll(products);
    }

    /**
     * Delete Product entity
     *
     * @param product Product entity to delete
     */

    @Override
    public void delete(Product product) {
        log.info("ProductServiceImpl.delete: delete product = {}", product);
        productRepository.deleteById(product.getId());
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            mqSuppliers.getUploadsDeleteObjectSink().emitNext(MessageBuilder
                    .withPayload(
                            UploadsDeleteObjectsDto.builder()
                                    .object_names(product.getImages())
                                    .type(UploadDto.Type.PRODUCT_IMAGE)
                                    .build()
                    ).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        }
        super.writeEvent(mqSuppliers, "Deleted product: %s (ID %d)".formatted(product.getName(), product.getId()));
    }

    /**
     * Update product rating
     *
     * @param productUpdateRatingDto DTO with update data
     * @throws EntityNotFoundException Product entity not found
     */

    @Override
    public void updateProductRating(ProductUpdateRatingDto productUpdateRatingDto) throws EntityNotFoundException {
        log.info("ProductServiceImpl.updateProductRating: update product rating = {}", productUpdateRatingDto);
        Product product = productRepository.findById(productUpdateRatingDto.getId())
                .orElseThrow(() -> {
                    log.error("ProductServiceImpl.updateProductRating: Product with id {} not found",
                            productUpdateRatingDto.getId());
                    return new EntityNotFoundException("Product not found");
                });
        product.setRating(productUpdateRatingDto.getRating());
        productRepository.save(product);
    }
}
