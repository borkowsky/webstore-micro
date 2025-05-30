package net.rewerk.webstore.dto.request.brand;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandPatchDto {
    @Size(
            min = 2,
            max = 256,
            message = "{validation.product.name.length}"
    )
    private String name;
    @NotEmpty(message = "{validation.product.image.not_empty}")
    private String image;
}
