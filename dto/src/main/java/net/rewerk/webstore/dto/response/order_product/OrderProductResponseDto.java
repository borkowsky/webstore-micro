package net.rewerk.webstore.dto.response.order_product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductResponseDto {
    private Integer id;
    private ProductResponseDto product;
    private Integer productId;
    private Integer orderId;
    private Integer amount;
}
