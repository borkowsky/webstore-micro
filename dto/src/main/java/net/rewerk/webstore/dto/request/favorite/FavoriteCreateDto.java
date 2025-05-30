package net.rewerk.webstore.dto.request.favorite;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FavoriteCreateDto {
    @NotNull(message = "{validation.favorite.product_id.required}")
    @Positive(message = "{validation.favorite.product_id.positive}")
    private Integer product_id;
}
