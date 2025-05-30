package net.rewerk.webstore.products.feign.client.fallback;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductActualRatingResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewStatsReportResponseDto;
import net.rewerk.webstore.products.feign.client.ReviewsFeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Fallback class for ReviewsFeignClient
 *
 * @author rewerk
 */

@Component
@Slf4j
public class ReviewsFeignClientFallback implements ReviewsFeignClient {
    @Override
    public SinglePayloadResponseDto<ReviewStatsReportResponseDto> collectStats() {
        log.error("ReviewsFeignClient: collectStats fallback called");
        return SinglePayloadResponseDto.<ReviewStatsReportResponseDto>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .payload(ReviewStatsReportResponseDto.builder()
                        .new_reviews_count(0L)
                        .build())
                .build();
    }

    @Override
    public SinglePayloadResponseDto<ProductActualRatingResponseDto> getProductActualRating(Integer productId) {
        log.error("ReviewsFeignClient: getProductActualRating fallback called");
        throw new EntityNotFoundException("Failed to retrieve product actual rating");
    }
}
