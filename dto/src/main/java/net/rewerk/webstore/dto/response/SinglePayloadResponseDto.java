package net.rewerk.webstore.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.rewerk.webstore.dto.GenericBaseResponseDto;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SinglePayloadResponseDto<T> extends GenericBaseResponseDto<T> {
    private T payload;
}
