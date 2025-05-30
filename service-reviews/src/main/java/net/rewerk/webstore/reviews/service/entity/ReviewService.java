package net.rewerk.webstore.reviews.service.entity;

import net.rewerk.webstore.dto.request.review.ReviewCreateDto;
import net.rewerk.webstore.dto.request.review.ReviewProbeRequestDto;
import net.rewerk.webstore.dto.response.product.ProductActualRatingResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewLastRatingsDto;
import net.rewerk.webstore.dto.response.review.ReviewProbeResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewStatsReportResponseDto;
import net.rewerk.webstore.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    Review findById(Integer id);

    ReviewResponseDto create(ReviewCreateDto reviewCreateDto, UUID userId);

    void delete(Review review);

    ReviewProbeResponseDto canReview(ReviewProbeRequestDto probeDto, UUID userId);

    Page<ReviewResponseDto> findAll(Specification<Review> specification, Pageable pageable);

    List<String> findAllImagesByProductId(Integer productId);

    ReviewLastRatingsDto findLastRatingsByProductId(Integer productId);

    ProductActualRatingResponseDto findActualRatingByProductId(Integer productId);

    ReviewStatsReportResponseDto collectStats();
}
