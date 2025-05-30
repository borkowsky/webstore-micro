package net.rewerk.webstore.dto.request.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDto {
    @NotNull(message = "{validation.order.basket_ids.required}")
    @NotEmpty(message = "{validation.order.basket_ids.empty}")
    private List<Integer> basket_ids;
    @NotNull(message = "{validation.order.address_id.required}")
    private Integer address_id;
}
