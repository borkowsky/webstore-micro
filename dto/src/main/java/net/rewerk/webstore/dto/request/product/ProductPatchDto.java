package net.rewerk.webstore.dto.request.product;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPatchDto {
    @Size(
            min = 4,
            max = 64,
            message = "{validation.product.name.length}"
    )
    private String name;
    @Size(
            min = 12,
            max = 4086,
            message = "{validation.product.description.length}"
    )
    private String description;
    private Integer category_id;
    private Integer brand_id;
    @Positive(message = "{validation.product.price.positive}")
    private Double price;
    @PositiveOrZero(message = "{validation.product.discount_price.positive}")
    private Double discountPrice;
    private Integer balance;
    private String[] images;
    private String[] tags;
    private Boolean enabled = Boolean.TRUE;
}
