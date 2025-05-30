package net.rewerk.users.feign.client;

import net.rewerk.users.feign.client.fallback.ProductsFeignClientFallback;
import net.rewerk.users.feign.configuration.DefaultConfiguration;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for retrieve products information
 *
 * @author rewerk
 */

@FeignClient(
        name = "products-client",
        url = "${services.products.base_uri}",
        configuration = {
                DefaultConfiguration.class
        },
        fallback = ProductsFeignClientFallback.class
)
public interface ProductsFeignClient {

    /**
     * Method for retrieve product by identifier
     *
     * @param id Product identifier
     * @return Response with payload with single Product entity response DTO
     */

    @Cacheable(
            value = "caffeine",
            key = "'products-client-getProduct' + #id"
    )
    @GetMapping("/{id:\\d+}")
    SinglePayloadResponseDto<ProductResponseDto> getProduct(@PathVariable Integer id);

    /**
     * Method for retrieve list of products by identifiers
     *
     * @param ids List of Product identifiers
     * @return Response with payload with collection of Product entity response DTO
     */

    @Cacheable(
            value = "caffeine",
            key = "'products-client-getProductsByIds' + #ids"
    )
    @GetMapping("by_ids")
    PayloadResponseDto<ProductResponseDto> getProductsByIds(@RequestParam List<Integer> ids);
}
