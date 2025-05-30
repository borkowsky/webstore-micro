package net.rewerk.webstore.products.feign.client;

import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.payment.PaymentStatsReportResponseDto;
import net.rewerk.webstore.products.feign.configuration.DefaultConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Feign client for retrieve payments information
 *
 * @author rewerk
 */

@FeignClient(
        name = "payments-client",
        url = "${services.payments.base_uri}",
        configuration = {
                DefaultConfiguration.class
        }
)
public interface PaymentsFeignClient {

    /**
     * Method for collect payments midnight statistics
     *
     * @return Payload response with single PaymentStatsReportResponseDto
     */

    @GetMapping("collect_stats")
    SinglePayloadResponseDto<PaymentStatsReportResponseDto> collectStats();
}
