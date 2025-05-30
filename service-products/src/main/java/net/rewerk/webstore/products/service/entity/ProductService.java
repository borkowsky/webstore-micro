package net.rewerk.webstore.products.service.entity;

import net.rewerk.webstore.dto.request.product.ProductBatchPatchRequestDto;
import net.rewerk.webstore.dto.request.product.ProductCreateDto;
import net.rewerk.webstore.dto.request.product.ProductPatchDto;
import net.rewerk.webstore.dto.request.product.ProductUpdateRatingDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ProductService {
    Product findById(Integer id);

    Page<ProductResponseDto> findAll(Pageable pageable);

    Page<ProductResponseDto> findAll(Specification<Product> specification, Pageable pageable);

    Page<ProductResponseDto> search(String name, Pageable pageable);

    List<ProductResponseDto> findPopular();

    List<ProductResponseDto> findByIds(List<Integer> ids);

    List<ProductResponseDto> findAvailableByIds(List<Integer> ids);

    ProductResponseDto create(ProductCreateDto productCreateDto);

    void update(Product product, ProductPatchDto productPatchDto);

    void updateAll(ProductBatchPatchRequestDto productBatchPatchRequestDto);

    void delete(Product product);

    void updateProductRating(ProductUpdateRatingDto productUpdateRatingDto);
}
