package net.rewerk.users.feign.client;

import net.rewerk.users.feign.client.fallback.CategoriesFeignClientFallback;
import net.rewerk.users.feign.configuration.DefaultConfiguration;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for retrieve categories information
 *
 * @author rewerk
 */

@FeignClient(
        name = "categories-client",
        url = "${services.categories.base_uri}",
        configuration = {
                DefaultConfiguration.class
        },
        fallback = CategoriesFeignClientFallback.class
)
public interface CategoriesFeignClient {

    /**
     * Cacheable method for retrieve list of categories by products identifiers
     *
     * @param productIds List of Product identifiers
     * @return Response with payload with collection of Category entity response DTO
     */

    @Cacheable(
            value = "caffeine",
            key = "'categories-client-getCategoriesByProductIds' + #productIds"
    )
    @GetMapping("by_products")
    PayloadResponseDto<CategoryResponseDto> getCategoriesByProductIds(@RequestParam List<Integer> productIds);
}
