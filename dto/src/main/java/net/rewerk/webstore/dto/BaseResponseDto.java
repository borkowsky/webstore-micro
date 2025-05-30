package net.rewerk.webstore.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BaseResponseDto {
    private Integer code;
    private String message;
}
