package net.rewerk.webstore.reviews.service.entity.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.exception.UnprocessableOperation;
import net.rewerk.webstore.dto.mq.events.EventsWriteDto;
import net.rewerk.webstore.dto.request.product.ProductUpdateRatingDto;
import net.rewerk.webstore.dto.request.review.ReviewCreateDto;
import net.rewerk.webstore.dto.request.review.ReviewProbeRequestDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.dto.response.order_product.OrderProductResponseDto;
import net.rewerk.webstore.dto.response.product.ProductActualRatingResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewLastRatingsDto;
import net.rewerk.webstore.dto.response.review.ReviewProbeResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewStatsReportResponseDto;
import net.rewerk.webstore.entity.Review;
import net.rewerk.webstore.reviews.feign.client.OrdersFeignClient;
import net.rewerk.webstore.reviews.mq.Suppliers;
import net.rewerk.webstore.reviews.repository.ReviewRepository;
import net.rewerk.webstore.reviews.service.aggregator.ReviewAggregatorService;
import net.rewerk.webstore.reviews.service.entity.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Review entity service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private static final Integer MAX_IMAGES_TO_PRODUCT = 20;
    private final ReviewRepository reviewRepository;
    private final OrdersFeignClient ordersFeignClient;
    private final ReviewAggregatorService reviewAggregatorService;
    private final Suppliers mqSuppliers;

    /**
     * Find reviews by Review JPA specification and Spring Pageable request
     *
     * @param specification Review entity JPA specification
     * @param pageable      Spring Pageable object
     * @return Page of Review entity response DTO
     */

    @Override
    public Page<ReviewResponseDto> findAll(Specification<Review> specification, Pageable pageable) {
        log.info("ReviewServiceImpl.findAll: find page of reviews. specification and pageable = {}",
                pageable);
        return reviewAggregatorService.aggregate(
                reviewRepository.findAll(specification, pageable)
        );
    }

    /**
     * Find Review entity by identifier
     *
     * @param id Review identifier
     * @return Review entity
     * @throws EntityNotFoundException Review entity not found
     */

    @Override
    public Review findById(Integer id) throws EntityNotFoundException {
        log.info("ReviewServiceImpl.findById: find review by id = {}", id);
        return reviewRepository.findById(id).orElseThrow(() -> {
            log.error("ReviewServiceImpl.findById: review with id {} was not found", id);
            return new EntityNotFoundException("Review not found");
        });
    }

    /**
     * Create Review entity
     *
     * @param createDto DTO with create data
     * @param userId    User identifier
     * @return Created Review response DTO
     * @throws UnprocessableOperation Review already exists, product not found in order
     */

    @Override
    public ReviewResponseDto create(ReviewCreateDto createDto, UUID userId) throws UnprocessableOperation {
        log.info("ReviewServiceImpl.create: createDto = {}, userId = {}", createDto, userId);
        if (!this.canReview(createDto.getOrder_id(), createDto.getProduct_id(), userId)) {
            throw new UnprocessableOperation("Review already exists");
        }
        SinglePayloadResponseDto<OrderResponseDto> orderPayload = ordersFeignClient
                .getOrderById(createDto.getOrder_id());
        ProductResponseDto product = orderPayload.getPayload().getProducts().stream()
                .map(OrderProductResponseDto::getProduct)
                .filter(pProduct -> pProduct.getId().equals(createDto.getProduct_id()))
                .findFirst().orElseThrow(() -> {
                    log.error("ReviewServiceImpl.create: product with id {} was not found in order",
                            createDto.getProduct_id());
                    return new UnprocessableOperation("Product not found in order");
                });
        Review result = reviewRepository.save(Review.builder()
                .images(createDto.getImages())
                .text(createDto.getText())
                .orderId(orderPayload.getPayload().getId())
                .rating(createDto.getRating())
                .productId(product.getId())
                .userId(userId)
                .build());
        mqSuppliers.getProductUpdateRatingSink().emitNext(MessageBuilder
                .withPayload(ProductUpdateRatingDto.builder()
                        .id(result.getProductId())
                        .rating(reviewRepository.findActualAverageRatingByProductId(product.getId()))
                        .build())
                .build(), Sinks.EmitFailureHandler.FAIL_FAST);
        return reviewAggregatorService.aggregate(result);
    }

    /**
     * Delete Review entity
     *
     * @param review Review entity to delete
     */

    @Override
    public void delete(Review review) {
        log.info("ReviewServiceImpl.delete: remove review {}", review);
        this.writeEvent("Deleted review: ID %d".formatted(review.getId()));
        reviewRepository.delete(review);
    }

    /**
     * Check review creation availability by probe DTO
     *
     * @param probeDto DTO with probe parameters
     * @param userId   User identifier
     * @return Review probe response DTO
     * @throws UnprocessableOperation Order not found
     */

    @Override
    public ReviewProbeResponseDto canReview(ReviewProbeRequestDto probeDto, UUID userId)
            throws UnprocessableOperation {
        log.info("ReviewServiceImpl.canReview: probeDto = {}, userId = {}", probeDto, userId);
        SinglePayloadResponseDto<OrderResponseDto> orderPayload = ordersFeignClient
                .getOrderById(probeDto.getOrder_id());
        OrderResponseDto order = orderPayload.getPayload();
        if (!order.getUser().getId().equals(userId)) {
            log.error("ReviewServiceImpl.canReview: userId does not match order userId");
            throw new UnprocessableOperation("Order not found");
        }
        return ReviewProbeResponseDto.builder()
                .order_id(order.getId())
                .product_id(probeDto.getProduct_id())
                .allowed(this.canReview(order.getId(), probeDto.getProduct_id(), userId))
                .build();
    }

    /**
     * Check review creation availability by order, product and user identifiers
     *
     * @param orderId   Order entity identifier
     * @param productId Product entity identifier
     * @param userId    User entity identifier
     * @return Can review boolean result
     */

    private boolean canReview(Integer orderId, Integer productId, UUID userId) {
        log.info("ReviewServiceImpl.canReview: orderId = {}, productId = {}, userId = {}", orderId, productId, userId);
        return !reviewRepository.existsByOrderIdAndProductIdAndUserId(orderId, productId, userId);
    }

    /**
     * Find last ratings by product identifier
     *
     * @param productId Product entity identifier
     * @return DTO with review last ratings
     */

    @Override
    public ReviewLastRatingsDto findLastRatingsByProductId(Integer productId) {
        log.info("ReviewServiceImpl.findLastRatingsByProductId: find last ratings for product by productId = {}",
                productId);
        Map<Integer, Long> result = new HashMap<>();
        List<Integer> lastRatings = reviewRepository.findLastRatingsByProductId(productId);
        IntStream.range(1, 6).forEach(i -> result.put(i, (long) Collections.frequency(lastRatings, i)));
        return ReviewLastRatingsDto.builder()
                .total(lastRatings.size())
                .ratings(result)
                .build();
    }

    /**
     * Find actual rating by product identifier
     *
     * @param productId Product entity identifier
     * @return DTO with product actual rating
     */

    @Override
    public ProductActualRatingResponseDto findActualRatingByProductId(Integer productId) {
        log.info("ReviewServiceImpl.findActualRatingByProductId: find actual average rating fro product by id = {}",
                productId);
        return ProductActualRatingResponseDto.builder()
                .product_id(productId)
                .rating(reviewRepository.findActualAverageRatingByProductId(productId))
                .build();
    }

    /**
     * Collect review statistics for last day
     *
     * @return DTO with review statistics for last day
     */

    @Override
    public ReviewStatsReportResponseDto collectStats() {
        log.info("ReviewServiceImpl.collectStats: collecting reviews stats");
        Long reviewsCount = reviewRepository.countByCreatedAtGreaterThan(
                Date.from(LocalDate.now()
                        .atStartOfDay()
                        .toInstant(
                                ZoneId.systemDefault()
                                        .getRules()
                                        .getOffset(Instant.now())
                        )
                )
        );
        return ReviewStatsReportResponseDto.builder()
                .new_reviews_count(reviewsCount)
                .build();
    }

    /**
     * Find images by product identifier
     *
     * @param productId Product entity identifier
     * @return List of images URLs
     */

    @Override
    public List<String> findAllImagesByProductId(Integer productId) {
        log.info("ReviewServiceImpl.findAllImagesByProductId: find all reviews images by productId = {}", productId);
        List<List<String>> images = reviewRepository.findAllImagesByProductId(productId);
        return images.stream().flatMap(Collection::stream).limit(MAX_IMAGES_TO_PRODUCT).toList();
    }

    /**
     * Service method for send messages to SCS event writing queue
     *
     * @param text Event message text to send
     * @throws UnsupportedOperationException User not authenticated
     */

    private void writeEvent(String text) throws UnsupportedOperationException {
        log.info("ReviewServiceImpl.writeEvent: send message to write event SCS queue with text = {}", text);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnsupportedOperationException("No authentication found");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        mqSuppliers.getEventsWriteSink().emitNext(MessageBuilder.withPayload(EventsWriteDto.builder()
                .user_id(UUID.fromString(jwt.getSubject()))
                .text(text)
                .build()
        ).build(), Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
