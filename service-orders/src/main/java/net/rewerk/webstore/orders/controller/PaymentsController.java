package net.rewerk.webstore.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.payment.PaymentStatsReportResponseDto;
import net.rewerk.webstore.dto.response.payment.PaymentStatsResponseDto;
import net.rewerk.webstore.entity.Payment;
import net.rewerk.webstore.dto.request.payment.PaymentCreateDto;
import net.rewerk.webstore.dto.request.payment.PaymentSearchDto;
import net.rewerk.webstore.orders.service.entity.PaymentService;
import net.rewerk.webstore.orders.specification.PaymentSpecification;
import net.rewerk.webstore.utility.RequestUtils;
import net.rewerk.webstore.utility.ResponseUtils;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

/**
 * Not parametrized REST controller for Payment service
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsController {
    private final PaymentService paymentService;

    /**
     * Get all payments by search params
     *
     * @param paymentSearchDto Search DTO
     * @param jwt              oauth2 resource server Jwt token
     * @param authentication   Spring security authentication
     * @return ResponseEntity with status 200 OK and paginated payload of Payment entity response DTO
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<Payment>> getAllPayments(
            @Valid PaymentSearchDto paymentSearchDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        Page<Payment> result = paymentService.findAll(
                PaymentSpecification.getSpecification(paymentSearchDto, SecurityUtils.getUserFromJwtToken(
                        jwt, authentication
                )),
                RequestUtils.getSortAndPageRequest(paymentSearchDto)
        );
        return ResponseUtils.createPaginatedResponse(result);
    }

    /**
     * Get user Payment stats (today, total)
     *
     * @param jwt oauth2 resource server Jwt token
     * @return ResponseEntity with status 200 OK and single payload of payment stats response DTO
     */

    @GetMapping("stats")
    public ResponseEntity<SinglePayloadResponseDto<PaymentStatsResponseDto>> getPaymentStats(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseUtils.createSingleResponse(paymentService.getUserStats(UUID.fromString(jwt.getSubject())));
    }

    /**
     * Service endpoint for collect midnight scheduled job stats
     * Accessible only for service role
     *
     * @return ResponseEntity with status 200 OK and single payload of payment stats report entity response DTO
     */

    @GetMapping("collect_stats")
    public ResponseEntity<SinglePayloadResponseDto<PaymentStatsReportResponseDto>> collectPaymentStats() {
        return ResponseUtils.createSingleResponse(paymentService.collectStats());
    }

    /**
     * Endpoint for set Payment as paid
     *
     * @param paymentCreateDto Create DTO
     * @param jwt              oauth2 resource server Jwt token
     * @param uriBuilder       UriComponentsBuilder - autowired for building 201 Created location redirect
     * @return ResponseEntity with status 201 Created and payload of Payment entity
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<Payment>> createPayment(
            @Valid @RequestBody PaymentCreateDto paymentCreateDto,
            @AuthenticationPrincipal Jwt jwt,
            UriComponentsBuilder uriBuilder
    ) {
        Payment payment = paymentService.setAsPaid(paymentCreateDto.getPayment_id(), UUID.fromString(jwt.getSubject()));
        return ResponseEntity.created(uriBuilder
                .replacePath("/api/v1/payments/{paymentId}")
                .build(Map.of("paymentId", payment.getId()))
        ).body(SinglePayloadResponseDto.<Payment>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .payload(payment)
                .build());
    }
}
