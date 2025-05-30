package net.rewerk.webstore.dto.request.category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPatchDto {
    @Size(
            min = 4,
            max = 64,
            message = "{validation.category.name.length}"
    )
    private String name;
    private String description;
    @Size(
            min = 2,
            max = 64,
            message = "{validation.category.icon.length}"
    )
    private String icon;
    private Integer category_id;
    private Boolean enabled;
}
