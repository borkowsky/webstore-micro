package net.rewerk.webstore.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewStatsReportResponseDto {
    private Long new_reviews_count;
}
