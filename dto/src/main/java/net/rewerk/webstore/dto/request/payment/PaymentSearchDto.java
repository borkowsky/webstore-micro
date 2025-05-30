package net.rewerk.webstore.dto.request.payment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentSearchDto extends SortedRequestParamsDto {
    private String status;
    private UUID user_id;
}
