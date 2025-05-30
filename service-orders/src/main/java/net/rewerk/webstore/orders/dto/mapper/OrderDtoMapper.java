package net.rewerk.webstore.orders.dto.mapper;

import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderDtoMapper {
    OrderResponseDto toDto(Order order);

    List<OrderResponseDto> toDto(List<Order> orders);
}
