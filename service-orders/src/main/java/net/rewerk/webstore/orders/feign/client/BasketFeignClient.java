package net.rewerk.webstore.orders.feign.client;

import net.rewerk.webstore.dto.request.basket.BasketMultipleDeleteDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.orders.feign.client.fallback.BasketFeignClientFallback;
import net.rewerk.webstore.orders.feign.configuration.DefaultConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for retrieve Basket data
 *
 * @author rewerk
 */

@FeignClient(
        name = "basket-client",
        url = "${services.basket.base_uri}",
        configuration = {
                DefaultConfiguration.class
        },
        fallback = BasketFeignClientFallback.class
)
public interface BasketFeignClient {

    /**
     * Cacheable method for retrieve list of baskets by identifiers
     *
     * @param ids List of basket identifiers
     * @return Payload response of BasketResponseDto
     */

    @Cacheable(
            value = "caffeine",
            key = "'basket-client-getBasketsByIdsAndUserId' + #ids"
    )
    @GetMapping("by_ids")
    PayloadResponseDto<BasketResponseDto> getBasketsByIdsAndUserId(@RequestParam("ids") List<Integer> ids);

    /**
     * Method for delete Basket entities by identifiers
     *
     * @param basketMultipleDeleteDto Multiple Basket entity delete request DTO
     */

    @DeleteMapping
    void deleteBasketsByIds(@RequestBody BasketMultipleDeleteDto basketMultipleDeleteDto);
}
