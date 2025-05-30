package net.rewerk.webstore.orders.service.entity.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.exception.UnprocessableOperation;
import net.rewerk.webstore.dto.response.payment.PaymentStatsReportResponseDto;
import net.rewerk.webstore.dto.response.payment.PaymentStatsResponseDto;
import net.rewerk.webstore.entity.Order;
import net.rewerk.webstore.entity.Payment;
import net.rewerk.webstore.orders.repository.PaymentRepository;
import net.rewerk.webstore.orders.service.entity.OrderService;
import net.rewerk.webstore.orders.service.entity.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Payment service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    /**
     * Find payment by identifier
     *
     * @param id Payment identifier
     * @return Payment entity
     */

    @Override
    public Payment findById(Integer id) {
        log.info("PaymentServiceImpl.findById: id = {}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("PaymentServiceImpl.findById: payment with id {} not found", id);
                    return new EntityNotFoundException("Payment not found");
                });
    }


    /**
     * Method for retrieve page of payments
     *
     * @param specification Payment JPA specification for provide search
     * @param pageable      Pageable Spring object
     * @return Page of Payment entities
     */

    @Override
    public Page<Payment> findAll(Specification<Payment> specification, Pageable pageable) {
        log.info("PaymentServiceImpl.findAll");
        return paymentRepository.findAll(specification, pageable);
    }

    /**
     * Method for set payment as paid
     *
     * @param paymentId Payment identifier
     * @param userId    User identifier
     * @return Updated Payment entity
     */

    @Override
    public Payment setAsPaid(Integer paymentId, UUID userId) {
        log.info("PaymentServiceImpl.setAsPaid: paymentId = {}, userId = {}", paymentId, userId);
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> {
                    log.error("PaymentServiceImpl.setAsPaid: payment with id {} not found", paymentId);
                    return new EntityNotFoundException("Payment not found");
                });
        Order order = orderService.findByPaymentIdAndUserId(paymentId, userId);
        if (List.of(Order.Status.PAID, Order.Status.RECEIVED).contains(order.getStatus())) {
            log.error("PaymentServiceImpl.setAsPaid: order status is paid or received");
            throw new UnprocessableOperation("Order status conflict");
        }
        payment.setStatus(Payment.Status.APPROVED);
        order.setStatus(Order.Status.PAID);
        orderService.update(order);
        return paymentRepository.save(payment);
    }

    /**
     * Method for collect daily payments statistics
     *
     * @return PaymentStatsReportResponseDto with daily payments statistics
     */

    @Override
    public PaymentStatsReportResponseDto collectStats() {
        log.info("PaymentServiceImpl.collectStats called");
        List<Payment> payments = paymentRepository.findByCreatedAtGreaterThan(
                Date.from(LocalDate.now()
                        .atStartOfDay()
                        .toInstant(
                                ZoneId.systemDefault()
                                        .getRules()
                                        .getOffset(Instant.now())
                        )
                )
        );
        return PaymentStatsReportResponseDto.builder()
                .payments_sum(payments.stream().mapToDouble(Payment::getSum).sum())
                .new_payments_count((long) payments.size())
                .build();
    }

    /**
     * Method for collect user payments statistics
     *
     * @param userId User identifier
     * @return PaymentStatsResponseDto with user payments statistics
     */

    @Override
    public PaymentStatsResponseDto getUserStats(UUID userId) {
        log.info("PaymentServiceImpl.getUserStats: userId = {}", userId);
        Double todaySum = paymentRepository.getTotalSumByCreatedAtGreaterThanAndUserId(
                Date.from(LocalDate.now()
                        .atStartOfDay()
                        .toInstant(
                                ZoneId.systemDefault()
                                        .getRules()
                                        .getOffset(Instant.now())
                        )
                ),
                userId
        );
        Double totalSum = paymentRepository.getTotalSumByUserId(userId);
        return PaymentStatsResponseDto.builder()
                .userId(userId)
                .spent_today(todaySum)
                .spent_total(totalSum)
                .build();
    }
}
