package net.rewerk.webstore.dto.request.basket;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.request.PaginatedRequestParamsDto;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasketSearchDto extends PaginatedRequestParamsDto {
    private UUID user_id;
}
