package net.rewerk.webstore.orders.service.aggregator;

import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderAggregatorService {
    OrderResponseDto aggregate(Order order);

    List<OrderResponseDto> aggregate(List<Order> orders);

    Page<OrderResponseDto> aggregate(Page<Order> orders);
}
