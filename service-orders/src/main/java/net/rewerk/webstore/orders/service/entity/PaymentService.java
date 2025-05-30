package net.rewerk.webstore.orders.service.entity;

import net.rewerk.webstore.dto.response.payment.PaymentStatsReportResponseDto;
import net.rewerk.webstore.dto.response.payment.PaymentStatsResponseDto;
import net.rewerk.webstore.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface PaymentService {
    Payment findById(Integer id);

    Page<Payment> findAll(Specification<Payment> specification, Pageable pageable);

    Payment setAsPaid(Integer paymentId, UUID userId);

    PaymentStatsReportResponseDto collectStats();

    PaymentStatsResponseDto getUserStats(UUID userId);
}
