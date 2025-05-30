package net.rewerk.webstore.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewProbeResponseDto {
    private Integer order_id;
    private Integer product_id;
    private Boolean allowed;
}
