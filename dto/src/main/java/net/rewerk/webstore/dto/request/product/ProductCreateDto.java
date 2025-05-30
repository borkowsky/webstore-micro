package net.rewerk.webstore.dto.request.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    @NotNull(message = "{validation.product.name.required}")
    @Size(
            min = 4,
            max = 64,
            message = "{validation.product.name.length}"
    )
    private String name;
    @NotNull(message = "{validation.product.description.required}")
    @Size(
            min = 12,
            max = 4086,
            message = "{validation.product.description.length}"
    )
    private String description;
    @NotNull(message = "{validation.product.category_id.required}")
    private Integer category_id;
    @NotNull(message = "{validation.product.brand_id.required}")
    private Integer brand_id;
    @NotNull(message = "{validation.product.price.required}")
    @Positive(message =  "{validation.product.price.positive}")
    private Double price;
    @PositiveOrZero(message = "{validation.product.discount_price.positive_or_zero}")
    private Double discountPrice = 0.0d;
    @NotNull(message = "{validation.product.images.required}")
    private String[] images;
    private String[] tags;
    private Boolean enabled = Boolean.TRUE;
    @Setter(AccessLevel.NONE)
    private Double rating = 0.0d;
    @NotNull(message = "{validation.product.balance.required}")
    @PositiveOrZero(message = "{validation.product.balance.positive_or_zero}")
    private Integer balance;
}
