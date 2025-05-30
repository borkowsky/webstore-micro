package net.rewerk.webstore.orders.feign.client;

import net.rewerk.webstore.dto.request.product.ProductBatchPatchRequestDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.orders.feign.client.fallback.ProductsFeignClientFallback;
import net.rewerk.webstore.orders.feign.configuration.DefaultConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign client for retrieve Product data
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
     * Cacheable method for retrieve Product entity by identifier
     *
     * @param id Product identifier
     * @return Single payload of ProductResponseDto
     */

    @Cacheable(
            value = "caffeine",
            key = "'products-client-getProductById' + #id"
    )
    @GetMapping("{id:\\d+}")
    SinglePayloadResponseDto<ProductResponseDto> getProductById(@PathVariable("id") Integer id);

    /**
     * Cacheable method for retrieve Product entities by collection of identifiers
     *
     * @param ids List of Product identifiers
     * @return Payload response of ProductResponseDto
     */

    @Cacheable(
            value = "caffeine",
            key = "'products-client-getProductsByIds' + #ids"
    )
    @GetMapping("by_ids")
    PayloadResponseDto<ProductResponseDto> getProductsByIds(@RequestParam("ids") List<Integer> ids);

    @RequestMapping(method = RequestMethod.PATCH)
    void updateProducts(
            @RequestBody ProductBatchPatchRequestDto productBatchPatchRequestDto);
}
