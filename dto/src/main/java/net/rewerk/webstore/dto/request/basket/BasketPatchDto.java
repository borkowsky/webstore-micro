package net.rewerk.webstore.dto.request.basket;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BasketPatchDto {
    @Positive(message = "{validation.basket.amount.positive}")
    private Integer amount;
}
