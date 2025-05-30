package net.rewerk.webstore.dto.request.review;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReviewSearchDto extends SortedRequestParamsDto {
    @NotNull(message = "{validation.reviews.product_id.required}")
    private Integer product_id;
    private Integer user_id;
}