package net.rewerk.users.feign.client.fallback;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.users.feign.client.ProductsFeignClient;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fallback class for OrdersFeignClient
 *
 * @author rewerk
 */

@Component
@Slf4j
public class ProductsFeignClientFallback implements ProductsFeignClient {
    @Override
    public SinglePayloadResponseDto<ProductResponseDto> getProduct(Integer id) {
        log.error("ProductsFeignClient: getProduct fallback called. id: {}", id);
        throw new EntityNotFoundException("Product not found");
    }

    @Override
    public PayloadResponseDto<ProductResponseDto> getProductsByIds(List<Integer> ids) {
        log.info("ProductsFeignClient: getProductsByIds fallback called. ids: {}", ids);
        throw new EntityNotFoundException("Products not found");
    }
}
