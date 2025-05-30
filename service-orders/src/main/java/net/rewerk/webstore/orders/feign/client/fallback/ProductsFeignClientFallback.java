package net.rewerk.webstore.orders.feign.client.fallback;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.request.product.ProductBatchPatchRequestDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.orders.feign.client.ProductsFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductsFeignClientFallback implements ProductsFeignClient {
    @Override
    public SinglePayloadResponseDto<ProductResponseDto> getProductById(Integer id) {
        log.error("ProductsFeign: fallback getProductById called. id: {}", id);
        throw new EntityNotFoundException("Product not found");
    }

    @Override
    public PayloadResponseDto<ProductResponseDto> getProductsByIds(List<Integer> ids) {
        log.error("ProductsFeign: fallback getProductsByIds called. ids: {}", ids);
        throw new EntityNotFoundException("Products not found");
    }

    @Override
    public void updateProducts(ProductBatchPatchRequestDto productBatchPatchRequestDto) {
        log.error("ProductsFeign: fallback updateProducts called. dto: {}", productBatchPatchRequestDto);
        throw new BadRequestException("Update products failed");
    }
}
