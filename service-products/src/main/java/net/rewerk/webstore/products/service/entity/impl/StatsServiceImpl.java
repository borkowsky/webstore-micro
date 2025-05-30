package net.rewerk.webstore.products.service.entity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.order.OrderStatsReportResponseDto;
import net.rewerk.webstore.dto.response.payment.PaymentStatsReportResponseDto;
import net.rewerk.webstore.dto.response.review.ReviewStatsReportResponseDto;
import net.rewerk.webstore.dto.response.stats.StatsResponseDto;
import net.rewerk.webstore.entity.Stats;
import net.rewerk.webstore.products.dto.mapper.StatsDtoMapper;
import net.rewerk.webstore.products.feign.client.OrdersFeignClient;
import net.rewerk.webstore.products.feign.client.PaymentsFeignClient;
import net.rewerk.webstore.products.feign.client.ReviewsFeignClient;
import net.rewerk.webstore.products.repository.StatsRepository;
import net.rewerk.webstore.products.service.entity.StatsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Stats entity service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final OrdersFeignClient ordersFeignClient;
    private final ReviewsFeignClient reviewsFeignClient;
    private final PaymentsFeignClient paymentsFeignClient;
    private final StatsDtoMapper statsDtoMapper;

    /**
     * Find stats by Spring Pageable request and Stats entity JPA specification
     *
     * @param pageable      Spring Pageable object
     * @param specification Stats JPA specification
     * @return Page of Stats entity response DTO
     */

    @Override
    public Page<StatsResponseDto> findAll(Pageable pageable, Specification<Stats> specification) {
        log.info("StatsServiceImpl.findAll: Finding all stats. specification and pageable = {}",
                pageable);
        Page<Stats> page = statsRepository.findAll(specification, pageable);
        return new PageImpl<>(statsDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Collect stats from order, review, payments services
     * Scheduled to launch every midnight
     */

    @Scheduled(cron = "@daily")
    public void collectStats() {
        log.info("StatsServiceImpl.collectStats: Collecting stats...");
        SinglePayloadResponseDto<OrderStatsReportResponseDto> ordersStatsPayload = ordersFeignClient
                .collectStats();
        SinglePayloadResponseDto<ReviewStatsReportResponseDto> reviewsStatsPayload = reviewsFeignClient
                .collectStats();
        SinglePayloadResponseDto<PaymentStatsReportResponseDto> paymentsStatsPayload = paymentsFeignClient
                .collectStats();
        statsRepository.save(Stats.builder()
                .orders(ordersStatsPayload.getPayload().getNew_orders_count())
                .paid(paymentsStatsPayload.getPayload().getPayments_sum())
                .reviews(reviewsStatsPayload.getPayload().getNew_reviews_count())
                .users(0L)
                .build());
    }
}
