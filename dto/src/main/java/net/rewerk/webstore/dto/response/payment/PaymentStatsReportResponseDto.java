package net.rewerk.webstore.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatsReportResponseDto {
    private Long new_payments_count;
    private Double payments_sum;
}
