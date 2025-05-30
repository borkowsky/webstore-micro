package net.rewerk.users.feign.client.fallback;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.users.feign.client.CategoriesFeignClient;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fallback class for CategoriesFeignClient
 *
 * @author rewerk
 */

@Component
@Slf4j
public class CategoriesFeignClientFallback implements CategoriesFeignClient {

    @Override
    public PayloadResponseDto<CategoryResponseDto> getCategoriesByProductIds(List<Integer> productIds) {
        log.error("CategoriesFeignClient: getCategoriesByProductIds fallback called. productIds: {}", productIds);
        throw new EntityNotFoundException("Categories not found");
    }
}
