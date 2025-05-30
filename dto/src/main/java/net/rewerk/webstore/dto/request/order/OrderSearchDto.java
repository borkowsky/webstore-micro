package net.rewerk.webstore.dto.request.order;

import lombok.*;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearchDto extends SortedRequestParamsDto implements Cloneable {
    private UUID user_id;
    private String type;

    @Override
    public OrderSearchDto clone() {
        try {
            return (OrderSearchDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
