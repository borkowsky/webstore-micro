package net.rewerk.webstore.reviews.service.aggregator;

import net.rewerk.webstore.dto.response.review.ReviewResponseDto;
import net.rewerk.webstore.entity.Review;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewAggregatorService {
    ReviewResponseDto aggregate(Review dto);

    List<ReviewResponseDto> aggregate(List<Review> dto);

    Page<ReviewResponseDto> aggregate(Page<Review> dto);
}
