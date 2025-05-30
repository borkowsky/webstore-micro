package net.rewerk.webstore.dto.request.review;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class ReviewCreateDto {
    @NotNull(message = "{validation.reviews.order_id.required}")
    @Positive(message = "{validation.reviews.order_id.positive}")
    private Integer order_id;
    @NotNull(message = "{validation.reviews.product_id.required}")
    @Positive(message = "{validation.reviews.product_id.positive}")
    private Integer product_id;
    @NotNull(message = "{validation.reviews.text.required}")
    @Size(
            min = 6,
            max = 2048,
            message = "{validation.reviews.text.length}"
    )
    private String text;
    @NotNull(message = "{validation.reviews.rating.required}")
    @Min(1)
    @Max(5)
    private Integer rating;
    @NotNull(message = "{validation.reviews.images.required}")
    @Size(
            max = 5,
            message = "{validation.reviews.images.max}"
    )
    private List<String> images;
}
