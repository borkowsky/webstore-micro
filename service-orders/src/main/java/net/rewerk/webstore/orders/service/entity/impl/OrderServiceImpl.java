package net.rewerk.webstore.orders.service.entity.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.exception.UnprocessableOperation;
import net.rewerk.webstore.dto.request.basket.BasketMultipleDeleteDto;
import net.rewerk.webstore.dto.request.order.OrderCreateDto;
import net.rewerk.webstore.dto.request.order.OrderPatchDto;
import net.rewerk.webstore.dto.request.order.OrderSearchDto;
import net.rewerk.webstore.dto.request.product.ProductBatchPatchRequestDto;
import net.rewerk.webstore.dto.request.product.ProductPatchDto;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.dto.response.order.OrderCountersResponseDto;
import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.dto.response.order.OrderStatsReportResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Order;
import net.rewerk.webstore.entity.OrdersProducts;
import net.rewerk.webstore.entity.Payment;
import net.rewerk.webstore.entity.User;
import net.rewerk.webstore.orders.dto.mapper.OrderDtoMapper;
import net.rewerk.webstore.orders.feign.client.AddressesFeignClient;
import net.rewerk.webstore.orders.feign.client.BasketFeignClient;
import net.rewerk.webstore.orders.feign.client.ProductsFeignClient;
import net.rewerk.webstore.orders.mq.Suppliers;
import net.rewerk.webstore.orders.repository.OrderRepository;
import net.rewerk.webstore.orders.service.EventWritingService;
import net.rewerk.webstore.orders.service.aggregator.OrderAggregatorService;
import net.rewerk.webstore.orders.service.entity.OrderService;
import net.rewerk.webstore.orders.specification.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/**
 * Order service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class OrderServiceImpl extends EventWritingService implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDtoMapper orderDtoMapper;
    private final AddressesFeignClient addressesFeignClient;
    private final BasketFeignClient basketFeignClient;
    private final ProductsFeignClient productsFeignClient;
    private final OrderAggregatorService orderAggregatorService;
    private final Suppliers mqSuppliers;

    /**
     * Method for get Order by identifier
     *
     * @param orderId Order identifier
     * @param user    Authenticated User
     * @return Order entity
     * @throws EntityNotFoundException If entity not found
     */

    @Override
    public Order findById(
            @NonNull Integer orderId, @NonNull User user) throws EntityNotFoundException {
        log.info("OrderServiceImpl.findById: finding order with id {}. User role: {}", orderId, user.getRole());
        if (User.Role.ROLE_USER.equals(user.getRole())) {
            return orderRepository.findByIdAndUserId(orderId, user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        }
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    /**
     * Method for retrieve Order entity by Payment identifier and User identifier
     *
     * @param paymentId Payment identifier
     * @param userId    User identifier
     * @return Order entity
     * @throws EntityNotFoundException If entity not found
     */

    @Override
    public Order findByPaymentIdAndUserId(Integer paymentId, UUID userId) throws EntityNotFoundException {
        log.info("OrderServiceImpl.findByPaymentIdAndUserId: finding order with id {}, userId {}", paymentId, userId);
        return orderRepository.findByPaymentIdAndUserId(paymentId, userId)
                .orElseThrow(() -> {
                    log.error("OrderServiceImpl.findByPaymentIdAndUserId: order with id {} not found", paymentId);
                    return new EntityNotFoundException("Order not found");
                });
    }

    /**
     * Method for retrieve Order counters (active, completed)
     *
     * @param dto  Order Search DTO with search parameters
     * @param user Authenticated User
     * @return Order counters response DTO
     */

    @Override
    public OrderCountersResponseDto getCounters(@NonNull OrderSearchDto dto, @NonNull User user) {
        log.info("OrderServiceImpl.getCounters: getting counters for {}, user.id: {}", dto, user.getId());
        OrderSearchDto activeDto = dto.clone();
        OrderSearchDto completedDto = dto.clone();
        activeDto.setType("active");
        completedDto.setType("completed");
        Long activeTotal = orderRepository.count(OrderSpecification.getSpecification(user, activeDto));
        Long completedTotal = orderRepository.count(OrderSpecification.getSpecification(user, completedDto));
        return OrderCountersResponseDto.builder()
                .active(activeTotal)
                .completed(completedTotal)
                .build();
    }

    /**
     * Method for create new Order
     *
     * @param orderCreateDto Order Create DTO with create data
     * @param user           Authenticated user
     * @return Order response DTO
     * @throws UnprocessableOperation  User not have address, product not available, product not have enough balance, failed to update product, failed to delete baskets
     * @throws EntityNotFoundException Address not found, basket not found
     */

    @Override
    public OrderResponseDto create(OrderCreateDto orderCreateDto, User user)
            throws UnprocessableOperation, EntityNotFoundException {
        log.info("OrderServiceImpl.create: creating order {}, user.id {}", orderCreateDto, user.getId());
        PaginatedPayloadResponseDto<AddressResponseDto> addressesPayload = addressesFeignClient
                .getAddresses(user.getId());
        List<AddressResponseDto> addresses = addressesPayload.getPayload();
        if (addresses.isEmpty()) {
            log.error("OrderServiceImpl.create: addresses is empty");
            throw new UnprocessableOperation("User not have any address");
        }
        AddressResponseDto address = addresses.stream()
                .filter(a -> Objects.equals(a.getId(), orderCreateDto.getAddress_id()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("OrderServiceImpl.create: address not found");
                    return new EntityNotFoundException("Address not found");
                });
        PayloadResponseDto<BasketResponseDto> basketPayload = basketFeignClient.getBasketsByIdsAndUserId(
                orderCreateDto.getBasket_ids()
        );
        List<BasketResponseDto> baskets = basketPayload.getPayload().stream()
                .filter(Objects::nonNull)
                .filter(basket -> basket.getUserId().equals(user.getId()))
                .toList();
        if (baskets.isEmpty()) {
            log.error("OrderServiceImpl.create: basket not found");
            throw new EntityNotFoundException("Basket not found");
        }
        if (!baskets.stream().map(BasketResponseDto::getProduct).allMatch(ProductResponseDto::isAvailable)) {
            log.error("OrderServiceImpl.create: product is not available");
            throw new UnprocessableOperation("Product not available");
        }
        if (!baskets.stream()
                .allMatch(basket -> basket.getProduct().getBalance() >= basket.getAmount())) {
            log.error("OrderServiceImpl.create: not enough products balance");
            throw new UnprocessableOperation("Not enough products balance");
        }
        ProductBatchPatchRequestDto productBatchPatchRequestDto = new ProductBatchPatchRequestDto();
        Map<Integer, ProductPatchDto> productPatchDtoMap = new HashMap<>();
        baskets.forEach(basket -> {
            ProductResponseDto product = basket.getProduct();
            productPatchDtoMap.put(product.getId(), ProductPatchDto.builder()
                    .balance(product.getBalance() - basket.getAmount())
                    .build());
        });
        productBatchPatchRequestDto.setData(productPatchDtoMap);
        try {
            productsFeignClient.updateProducts(productBatchPatchRequestDto);
        } catch (Exception e) {
            throw new UnprocessableOperation(e.getMessage());
        }
        Double totalSum = baskets.stream()
                .filter(basket -> Objects.nonNull(basket.getProduct()))
                .mapToDouble(
                        basket -> basket.getAmount() * (
                                basket.getProduct().getDiscountPrice() != null
                                        && basket.getProduct().getDiscountPrice() < basket.getProduct().getPrice() ?
                                        basket.getProduct().getDiscountPrice() :
                                        basket.getProduct().getPrice()
                        )
                )
                .sum();
        Order order = Order.builder()
                .userId(user.getId())
                .status(Order.Status.CREATED)
                .payment(Payment.builder()
                        .status(Payment.Status.CREATED)
                        .sum(totalSum)
                        .userId(user.getId())
                        .build())
                .products(baskets.stream()
                        .map(basket -> OrdersProducts.builder()
                                .productId(basket.getProduct().getId())
                                .amount(basket.getAmount())
                                .build())
                        .toList()
                )
                .addressId(address.getId())
                .build();
        try {
            basketFeignClient.deleteBasketsByIds(
                    BasketMultipleDeleteDto.builder()
                            .basket_ids(baskets.stream()
                                    .map(BasketResponseDto::getId)
                                    .toList())
                            .user_id(user.getId())
                            .build());
        } catch (Exception e) {
            throw new UnprocessableOperation(e.getMessage());
        }
        Order result = orderRepository.save(order);
        return orderDtoMapper.toDto(result);
    }

    /**
     * Method for update Order entity
     *
     * @param order Order entity
     */

    @Override
    public void update(Order order) {
        log.info("OrderServiceImpl.update: order = {}", order);
        orderRepository.save(order);
        super.writeEvent(mqSuppliers, "Updated order: ID %d".formatted(order.getId()));
    }

    /**
     * Method for update Order entity by Patch DTO
     *
     * @param order         Order entity
     * @param orderPatchDto Order patch DTO with patch data
     */

    @Override
    public void update(Order order, OrderPatchDto orderPatchDto) {
        log.info("OrderServiceImpl.update: order.id = {}, dto = {}", order.getId(), orderPatchDto);
        if (orderPatchDto.getOrder_status() != null) {
            order.setStatus(orderPatchDto.getOrder_status());
        }
        if (orderPatchDto.getPayment_status() != null) {
            order.getPayment().setStatus(orderPatchDto.getPayment_status());
        }
        this.update(order);
    }

    /**
     * Method for retrieve Order entities
     *
     * @param specification Order JPA specification to search
     * @param pageable      Pageable Spring object
     * @return Page of Order response DTO
     */

    @Override
    public Page<OrderResponseDto> findAll(Specification<Order> specification, Pageable pageable) {
        log.info("OrderServiceImpl.findAll: specification and pageable = {}", pageable);
        return orderAggregatorService.aggregate(orderRepository.findAll(specification, pageable));
    }

    /**
     * Method for collect orders statistics
     *
     * @return Order stats report response DTO
     */

    @Override
    public OrderStatsReportResponseDto collectStats() {
        log.info("OrderServiceImpl.collectStats called");
        Long orderCount = orderRepository.countByCreatedAtGreaterThan(
                Date.from(LocalDate.now()
                        .atStartOfDay()
                        .toInstant(
                                ZoneId.systemDefault()
                                        .getRules()
                                        .getOffset(Instant.now())
                        )
                )
        );
        return OrderStatsReportResponseDto.builder()
                .new_orders_count(orderCount)
                .build();
    }
}
