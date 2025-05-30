package net.rewerk.webstore.reviews.feign.client;

import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.reviews.feign.client.fallback.OrdersFeignClientFallback;
import net.rewerk.webstore.reviews.feign.configuration.DefaultConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for retrieve orders information
 *
 * @author rewerk
 */

@FeignClient(
        name = "orders-client",
        url = "${services.orders.base_uri}",
        configuration = {
                DefaultConfiguration.class
        },
        fallback = OrdersFeignClientFallback.class
)
public interface OrdersFeignClient {

    /**
     * Cacheable method for retrieve order by identifier
     *
     * @param id Order identifier
     * @return Response with single payload with Order entity response DTO
     */

    @Cacheable(
            value = "caffeine",
            key = "'orders-client-getOrderById' + #id"
    )
    @GetMapping("{id:\\d+}")
    SinglePayloadResponseDto<OrderResponseDto> getOrderById(@PathVariable("id") Integer id);
}
