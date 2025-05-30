package net.rewerk.webstore.dto.request.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDto {
    @NotNull(message = "{validation.category.name.required}")
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
            message = "{validation.category.icon.required}"
    )
    private String icon;
    private Integer category_id;
    private Boolean active = Boolean.TRUE;
    private Boolean enabled = Boolean.TRUE;
}
