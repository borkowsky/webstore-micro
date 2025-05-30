package net.rewerk.webstore.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatsResponseDto {
    private UUID userId;
    private Double spent_today;
    private Double spent_total;
}
