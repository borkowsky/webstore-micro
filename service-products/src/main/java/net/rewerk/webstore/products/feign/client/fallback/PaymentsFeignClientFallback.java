package net.rewerk.webstore.products.feign.client.fallback;

import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.payment.PaymentStatsReportResponseDto;
import net.rewerk.webstore.products.feign.client.PaymentsFeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Fallback class for PaymentsFeignClient
 *
 * @author rewerk
 */

@Component
@Slf4j
public class PaymentsFeignClientFallback implements PaymentsFeignClient {
    @Override
    public SinglePayloadResponseDto<PaymentStatsReportResponseDto> collectStats() {
        log.error("PaymentsFeignClient: collectStats fallback called");
        return SinglePayloadResponseDto.<PaymentStatsReportResponseDto>builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .payload(PaymentStatsReportResponseDto.builder()
                        .new_payments_count(0L)
                        .payments_sum(0.0d)
                        .build())
                .build();
    }
}
