package net.rewerk.webstore.dto.request.order;

import lombok.Data;
import net.rewerk.webstore.entity.Order;
import net.rewerk.webstore.entity.Payment;

@Data
public class OrderPatchDto {
    private Order.Status order_status;
    private Payment.Status payment_status;
}
