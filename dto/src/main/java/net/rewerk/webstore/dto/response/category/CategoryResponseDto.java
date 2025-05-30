package net.rewerk.webstore.dto.response.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {
    private Integer id;
    private String name;
    private String description;
    private String icon;
    private Integer categoryId;
    private List<CategoryResponseDto> categories;
    private Integer productsCount;
    private Boolean enabled;
}
