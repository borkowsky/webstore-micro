package net.rewerk.webstore.reviews.feign.client.fallback;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.reviews.feign.client.OrdersFeignClient;
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
    public SinglePayloadResponseDto<OrderResponseDto> getOrderById(Integer id) {
        log.error("OrdersFeignClient: getOrderById fallback called with id {}", id);
        throw new EntityNotFoundException("Order with id " + id + " not found");
    }
}
