package net.rewerk.webstore.reviews.service.aggregator.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.entity.Review;
import net.rewerk.webstore.reviews.dto.mapper.ReviewDtoMapper;
import net.rewerk.webstore.reviews.feign.client.UsersFeignClient;
import net.rewerk.webstore.reviews.service.aggregator.ReviewAggregatorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Data aggregate service for Review responses
 * Aggregated data: user
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Component
@Slf4j
public class ReviewAggregatorServiceImpl implements ReviewAggregatorService {
    private final UsersFeignClient usersFeignClient;
    private final ReviewDtoMapper reviewDtoMapper;

    /**
     * Single Review entity response data aggregator
     *
     * @param review Review entity to aggregate
     * @return Review entity response DTO with aggregated data
     */

    @Override
    public ReviewResponseDto aggregate(Review review) {
        log.info("ReviewAggregatorServiceImpl.aggregate: Aggregating review {}", review);
        SinglePayloadResponseDto<UserResponseDto> userPayload = usersFeignClient.getById(review.getUserId());
        ReviewResponseDto mapped = reviewDtoMapper.toDto(review);
        mapped.setUser(userPayload.getPayload());
        return mapped;
    }

    /**
     * List of Review entities response data aggregator
     *
     * @param reviews List of Review entities
     * @return List of Review entities response DTO with aggregated data
     */

    @Override
    public List<ReviewResponseDto> aggregate(List<Review> reviews) {
        log.info("ReviewAggregatorServiceImpl.aggregate: Aggregating list of reviews: SIZE = {}", reviews.size());
        List<UUID> uuids = reviews.stream().map(Review::getUserId).distinct().toList();
        PayloadResponseDto<UserResponseDto> usersPayload = usersFeignClient.getByIds(uuids);
        List<ReviewResponseDto> mapped = reviewDtoMapper.toDto(reviews);
        mapped.forEach(review -> {
            Optional<UserResponseDto> user = usersPayload.getPayload().stream()
                    .filter(usr -> review.getUserId().equals(usr.getId()))
                    .findFirst();
            user.ifPresent(review::setUser);
        });
        return mapped;
    }

    /**
     * Page of Review entities response data aggregator
     *
     * @param reviews Page of Review entities
     * @return Page of Review entities response DTO with aggregated data
     */

    @Override
    public Page<ReviewResponseDto> aggregate(Page<Review> reviews) {
        log.info("ReviewAggregatorServiceImpl.aggregate: Aggregating page of reviews: SIZE = {}", reviews.getSize());
        return new PageImpl<>(this.aggregate(reviews.getContent()), reviews.getPageable(), reviews.getTotalElements());
    }
}
