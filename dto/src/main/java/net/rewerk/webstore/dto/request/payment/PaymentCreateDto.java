package net.rewerk.webstore.dto.request.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentCreateDto {
    @NotNull(message = "{validation.payment.payment_id.required}")
    @Positive(message = "{validation.payment.payment_id.positive}")
    private Integer payment_id;
}
