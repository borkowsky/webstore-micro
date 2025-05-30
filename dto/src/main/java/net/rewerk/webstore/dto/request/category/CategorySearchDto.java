package net.rewerk.webstore.dto.request.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySearchDto extends SortedRequestParamsDto {
    private Integer category_id = -1;
    private Boolean enabled;
}
