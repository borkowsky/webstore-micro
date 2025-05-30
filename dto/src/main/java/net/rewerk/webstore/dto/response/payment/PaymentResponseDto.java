package net.rewerk.webstore.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.entity.Payment;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private Integer id;
    private UUID userId;
    private Double sum;
    private Payment.Status status;
    private Date createdAt;
    private Date updatedAt;
}
