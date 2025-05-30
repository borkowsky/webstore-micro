package net.rewerk.webstore.dto.response.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.response.order_product.OrderProductResponseDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.dto.response.payment.PaymentResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.entity.Order;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Integer id;
    @JsonIgnore
    private UUID userId;
    private UserResponseDto user;
    private List<OrderProductResponseDto> products;
    private PaymentResponseDto payment;
    @JsonIgnore
    private Integer addressId;
    private AddressResponseDto address;
    private Order.Status status;
    private Date createdAt;
    private Date updatedAt;
}
