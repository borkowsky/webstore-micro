package net.rewerk.webstore.orders.service.entity;

import net.rewerk.webstore.dto.response.order.OrderStatsReportResponseDto;
import net.rewerk.webstore.entity.Order;
import net.rewerk.webstore.entity.User;
import net.rewerk.webstore.dto.request.order.OrderCreateDto;
import net.rewerk.webstore.dto.request.order.OrderPatchDto;
import net.rewerk.webstore.dto.request.order.OrderSearchDto;
import net.rewerk.webstore.dto.response.order.OrderCountersResponseDto;
import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface OrderService {
    Order findById(Integer orderId, User user);

    Order findByPaymentIdAndUserId(Integer paymentId, UUID userId);

    OrderCountersResponseDto getCounters(OrderSearchDto dto, User user);

    OrderResponseDto create(OrderCreateDto orderCreateDto, User user);

    void update(Order order);

    void update(Order order, OrderPatchDto orderPatchDto);

    Page<OrderResponseDto> findAll(Specification<Order> specification, Pageable pageable);

    OrderStatsReportResponseDto collectStats();
}
