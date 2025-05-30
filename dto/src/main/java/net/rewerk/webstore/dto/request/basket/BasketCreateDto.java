package net.rewerk.webstore.dto.request.basket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketCreateDto {
    @NotNull(message = "{validation.basket.product_id.required}")
    private Integer product_id;
    @NotNull(message = "{validation.basket.amount.required}")
    @Positive(message = "{validation.basket.amount.positive}")
    private Integer amount;
}
