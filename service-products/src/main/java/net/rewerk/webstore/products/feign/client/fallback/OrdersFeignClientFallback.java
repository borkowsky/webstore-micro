package net.rewerk.webstore.products.feign.client.fallback;

import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.order.OrderStatsReportResponseDto;
import net.rewerk.webstore.products.feign.client.OrdersFeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Fallback class for OrdersFeignClient
 *
 * @author rewerk
 */

@Component
@Slf4j
public class OrdersFeignClientFallback implements OrdersFeignClient {
    @Override
    public SinglePayloadResponseDto<OrderStatsReportResponseDto> collectStats() {
        log.error("OrdersFeignClient: collectStats fallback called");
        return SinglePayloadResponseDto.<OrderStatsReportResponseDto>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .payload(OrderStatsReportResponseDto.builder()
                        .new_orders_count(0L)
                        .build())
                .build();
    }
}
