package net.rewerk.webstore.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import net.rewerk.webstore.dto.GenericBaseResponseDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedPayloadResponseDto<T> extends GenericBaseResponseDto<T> {
    private List<T> payload;
    private Long total;
    private Integer page;
    private Integer pages;
}
