package net.rewerk.webstore.dto.request.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewProbeRequestDto {
    @NotNull(message = "{validation.reviews.order_id.required}")
    @Positive(message = "{validation.reviews.order_id.positive}")
    private Integer order_id;
    @NotNull(message = "{validation.reviews.product_id.required}")
    @Positive(message = "{validation.reviews.product_id.positive}")
    private Integer product_id;
}
