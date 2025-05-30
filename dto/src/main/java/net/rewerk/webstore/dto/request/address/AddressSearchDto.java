package net.rewerk.webstore.dto.request.address;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddressSearchDto extends SortedRequestParamsDto {
    private UUID user_id;
}
