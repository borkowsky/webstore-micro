package net.rewerk.webstore.dto.request.favorite;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteSearchDto extends SortedRequestParamsDto {
    private UUID user_id;
}
