package net.rewerk.webstore.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchDto extends SortedRequestParamsDto {
    private Integer category_id = -1;
    private Integer brand_id = -1;
    private Boolean available = true;
}
