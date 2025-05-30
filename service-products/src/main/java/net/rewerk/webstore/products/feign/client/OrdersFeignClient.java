package net.rewerk.webstore.products.feign.client;

import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.order.OrderStatsReportResponseDto;
import net.rewerk.webstore.products.feign.client.fallback.OrdersFeignClientFallback;
import net.rewerk.webstore.products.feign.configuration.DefaultConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

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
     * Method for collect orders midnight statistics
     *
     * @return Payload response with single OrderStatsReportResponseDto
     */

    @GetMapping("collect_stats")
    SinglePayloadResponseDto<OrderStatsReportResponseDto> collectStats();
}
