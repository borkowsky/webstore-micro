package net.rewerk.webstore.dto.request.brand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandSearchDto extends SortedRequestParamsDto {
    private String name_query;
    private Integer product_category_id;
}
