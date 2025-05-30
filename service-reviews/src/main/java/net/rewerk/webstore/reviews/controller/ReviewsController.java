package net.rewerk.webstore.reviews.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.review.ReviewCreateDto;
import net.rewerk.webstore.dto.request.review.ReviewProbeRequestDto;
import net.rewerk.webstore.dto.request.review.ReviewSearchDto;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.product.ProductActualRatingResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewLastRatingsDto;
import net.rewerk.webstore.dto.response.review.ReviewProbeResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewStatsReportResponseDto;
import net.rewerk.webstore.reviews.service.entity.ReviewService;
import net.rewerk.webstore.reviews.specification.ReviewSpecification;
import net.rewerk.webstore.utility.RequestUtils;
import net.rewerk.webstore.utility.ResponseUtils;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for Review service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewsController {
    private final ReviewService reviewService;

    /**
     * GET endpoint for find reviews
     *
     * @param searchDto      DTO with search parameters
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Authentication object
     * @return Response with status 200 OK and paginated payload response with Review entity response DTO
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<ReviewResponseDto>> findReviews(
            @Valid ReviewSearchDto searchDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Page<ReviewResponseDto> reviews = reviewService.findAll(
                ReviewSpecification.getSpecification(searchDto, SecurityUtils.getUserFromJwtToken(jwt, authentication)),
                RequestUtils.getSortAndPageRequest(searchDto)
        );
        return ResponseUtils.createPaginatedResponse(reviews);
    }

    /**
     * GET endpoint for retrieve list of review images by search DTO
     *
     * @param searchDto DTO with search parameters
     * @return Response with status 200 OK and payload with list of images URLs
     */

    @GetMapping("/images")
    public ResponseEntity<PayloadResponseDto<String>> findReviewsImages(@Valid ReviewSearchDto searchDto) {
        List<String> result = reviewService.findAllImagesByProductId(searchDto.getProduct_id());
        return ResponseUtils.createCollectionResponse(result);
    }

    /**
     * GET endpoint for retrieve product list of last ratings
     *
     * @param searchDto DTO with search parameters
     * @return Response with status 200 OK and single payload with review last ratings DTO
     */

    @GetMapping("/last-ratings")
    public ResponseEntity<SinglePayloadResponseDto<ReviewLastRatingsDto>> findReviewsLastRatings(
            @Valid ReviewSearchDto searchDto
    ) {
        return ResponseUtils.createSingleResponse(reviewService.findLastRatingsByProductId(searchDto.getProduct_id()));
    }

    /**
     * GET endpoint for collect reviews stats
     *
     * @return Response with status 200 OK and single payload with reviews stats report response DTO
     */

    @GetMapping("collect_stats")
    public ResponseEntity<SinglePayloadResponseDto<ReviewStatsReportResponseDto>> collectStats() {
        return ResponseUtils.createSingleResponse(reviewService.collectStats());
    }

    /**
     * GET endpoint for retrieve product actual rating
     *
     * @param productId Product identifier
     * @return Response with status 200 OK and single payload with product actual rating DTO
     */

    @GetMapping("rating/{productId:\\d+}")
    public ResponseEntity<SinglePayloadResponseDto<ProductActualRatingResponseDto>> findProductActualRating(
            @PathVariable Integer productId
    ) {
        return ResponseUtils.createSingleResponse(reviewService.findActualRatingByProductId(productId));
    }

    /**
     * POST endpoint for create review
     *
     * @param createDto  DTO with create data
     * @param uriBuilder UriComponentsBuilder - autowired for create redirect location
     * @param jwt        OAuth2 jwt token
     * @return Response entity with status 201 Created and single payload with created Review entity response DTO
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<ReviewResponseDto>> createReview(
            @Valid @RequestBody ReviewCreateDto createDto,
            UriComponentsBuilder uriBuilder,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ReviewResponseDto review = reviewService.create(createDto, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.created(uriBuilder.replacePath("/api/v1/reviews/${reviewId}")
                .build(Map.of("reviewId", review.getId()))
        ).body(SinglePayloadResponseDto.<ReviewResponseDto>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(review)
                .build());
    }

    /**
     * POST mapping for check availability to create review for product
     *
     * @param probeDto DTO with probe parameters
     * @param jwt      OAuth2 jwt token
     * @return Response with status 200 OK and single payload with review probe response DTO
     */

    @PostMapping("/probe")
    public ResponseEntity<SinglePayloadResponseDto<ReviewProbeResponseDto>> probeReview(
            @Valid @RequestBody ReviewProbeRequestDto probeDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseUtils.createSingleResponse(reviewService.canReview(probeDto, UUID.fromString(jwt.getSubject())));
    }
}
