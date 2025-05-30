package net.rewerk.webstore.products.feign.client;

import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductActualRatingResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewStatsReportResponseDto;
import net.rewerk.webstore.products.feign.client.fallback.ReviewsFeignClientFallback;
import net.rewerk.webstore.products.feign.configuration.DefaultConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for retrieve reviews information
 *
 * @author rewerk
 */

@FeignClient(
        name = "reviews-client",
        url = "${services.reviews.base_uri}",
        configuration = {
                DefaultConfiguration.class
        },
        fallback = ReviewsFeignClientFallback.class
)
public interface ReviewsFeignClient {

    /**
     * Method for collect reviews midnight statistics
     *
     * @return Payload response with single ReviewStatsReportResponseDto
     */

    @GetMapping("collect_stats")
    SinglePayloadResponseDto<ReviewStatsReportResponseDto> collectStats();

    /**
     * Method for retrieve product actual rating by product identifier
     *
     * @param productId Product identifier
     * @return Payload response with single ProductActualRatingResponseDto
     */

    @GetMapping("rating/{productId:\\d+}")
    SinglePayloadResponseDto<ProductActualRatingResponseDto> getProductActualRating(
            @PathVariable("productId") Integer productId
    );
}
