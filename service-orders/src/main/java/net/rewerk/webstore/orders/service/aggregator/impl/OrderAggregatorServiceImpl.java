package net.rewerk.webstore.orders.service.aggregator.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.order_product.OrderProductResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.entity.Order;
import net.rewerk.webstore.entity.OrdersProducts;
import net.rewerk.webstore.orders.dto.mapper.OrderDtoMapper;
import net.rewerk.webstore.orders.feign.client.AddressesFeignClient;
import net.rewerk.webstore.orders.feign.client.ProductsFeignClient;
import net.rewerk.webstore.orders.feign.client.UsersFeignClient;
import net.rewerk.webstore.orders.service.aggregator.OrderAggregatorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Data aggregate service for Order responses
 * Aggregated data: list of products, user, address
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderAggregatorServiceImpl implements OrderAggregatorService {
    private final ProductsFeignClient productsFeignClient;
    private final AddressesFeignClient addressesFeignClient;
    private final UsersFeignClient usersFeignClient;
    private final OrderDtoMapper orderDtoMapper;

    /**
     * Aggregator method for single Order entity
     *
     * @param order Order entity to aggregate data
     * @return Order response DTO with aggregated data
     */

    @Override
    public OrderResponseDto aggregate(@NonNull Order order) {
        log.info("Aggregating order: ID {}", order.getId());
        PayloadResponseDto<ProductResponseDto> productPayload = productsFeignClient
                .getProductsByIds(order.getProducts().stream()
                        .map(OrdersProducts::getProductId)
                        .distinct()
                        .toList());
        PayloadResponseDto<AddressResponseDto> addressesPayload = addressesFeignClient
                .getAddressesById(List.of(order.getAddressId()));
        PayloadResponseDto<UserResponseDto> usersPayload = usersFeignClient.getByIds(List.of(order.getUserId()));
        return this.populateData(
                orderDtoMapper.toDto(order),
                productPayload.getPayload(),
                addressesPayload.getPayload(),
                usersPayload.getPayload()
        );
    }

    /**
     * Aggregator method for list of Order entity
     *
     * @param orders List of Order entity to aggregate data
     * @return List of Order response DTO with aggregated data
     */

    @Override
    public List<OrderResponseDto> aggregate(List<Order> orders) {
        log.info("Aggregating list of orders: SIZE {}", orders.size());
        PayloadResponseDto<ProductResponseDto> productsPayload = productsFeignClient
                .getProductsByIds(orders.stream()
                        .flatMap(order -> order.getProducts().stream())
                        .map(OrdersProducts::getProductId)
                        .distinct()
                        .toList()
                );
        PayloadResponseDto<AddressResponseDto> addressesPayload = addressesFeignClient
                .getAddressesById(orders.stream()
                        .map(Order::getAddressId)
                        .toList());
        PayloadResponseDto<UserResponseDto> usersPayload = usersFeignClient
                .getByIds(orders.stream()
                        .map(Order::getUserId)
                        .toList());
        return orders.stream()
                .map(order -> this.populateData(
                        orderDtoMapper.toDto(order),
                        productsPayload.getPayload(),
                        addressesPayload.getPayload(),
                        usersPayload.getPayload()
                ))
                .toList();
    }

    /**
     * Aggregator method for page of Order entity
     *
     * @param page Page of Order entity to aggregate data
     * @return Page of Order response DTO with aggregated data
     */

    @Override
    public Page<OrderResponseDto> aggregate(Page<Order> page) {
        log.info("Aggregating page of orders: SIZE {}", page.getSize());
        return new PageImpl<>(this.aggregate(page.getContent()), page.getPageable(), page.getTotalElements());
    }

    /**
     * Internal method for populate data to single Order entity
     *
     * @param order     Order entity to populate data
     * @param products  List of ProductResponseDto
     * @param addresses List of AddressResponseDto
     * @param users     List of UserResponseDto
     * @return Order response DTO populated with data
     */

    private OrderResponseDto populateData(OrderResponseDto order,
                                          List<ProductResponseDto> products,
                                          List<AddressResponseDto> addresses,
                                          List<UserResponseDto> users) {
        order.setProducts(order.getProducts()
                .stream()
                .map(orderProduct -> {
                    OrderProductResponseDto orderProductResponseDto = new OrderProductResponseDto();
                    orderProductResponseDto.setId(orderProduct.getId());
                    orderProductResponseDto.setOrderId(order.getId());
                    Optional<ProductResponseDto> product = products.stream()
                            .filter(p -> p.getId().equals(orderProduct.getProductId()))
                            .findFirst();
                    if (product.isPresent()) {
                        orderProductResponseDto.setProduct(product.get());
                        orderProductResponseDto.setAmount(orderProduct.getAmount());
                    } else {
                        orderProductResponseDto.setAmount(1);
                    }
                    return orderProductResponseDto;
                })
                .toList()
        );
        Optional<AddressResponseDto> address = addresses.stream()
                .filter(a -> order.getAddressId().equals(a.getId()))
                .findFirst();
        Optional<UserResponseDto> user = users.stream()
                .filter(u -> order.getUserId().equals(u.getId()))
                .findFirst();
        address.ifPresent(order::setAddress);
        user.ifPresent(order::setUser);
        return order;
    }
}
