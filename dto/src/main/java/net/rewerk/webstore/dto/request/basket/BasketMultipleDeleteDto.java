package net.rewerk.webstore.dto.request.basket;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketMultipleDeleteDto {
    @NotNull(message = "{validation.basket.ids.required}")
    @NotEmpty(message = "{validation.basket.ids.empty}")
    private List<Integer> basket_ids;
    private UUID user_id;
}
