package net.rewerk.webstore.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewLastRatingsDto {
    private Map<Integer, Long> ratings;
    private Integer total;
}
