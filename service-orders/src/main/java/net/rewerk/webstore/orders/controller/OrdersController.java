package net.rewerk.webstore.orders.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.request.order.OrderCreateDto;
import net.rewerk.webstore.dto.request.order.OrderSearchDto;
import net.rewerk.webstore.dto.response.order.OrderCountersResponseDto;
import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.dto.response.order.OrderStatsReportResponseDto;
import net.rewerk.webstore.orders.service.entity.OrderService;
import net.rewerk.webstore.orders.specification.OrderSpecification;
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

/**
 * Order - not parametrized REST controller
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {
    private final OrderService orderService;

    /**
     * Get all Orders
     *
     * @param orderSearchDto Order search DTO
     * @param jwt            oauth2 resource server Jwt token
     * @param authentication Spring security authentication
     * @return ResponseEntity with status 200 OK with paginated payload response of Order response DTO
     */

    @GetMapping
    public ResponseEntity<PaginatedPayloadResponseDto<OrderResponseDto>> getAllOrders(
            @Valid OrderSearchDto orderSearchDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Page<OrderResponseDto> result = orderService.findAll(
                OrderSpecification.getSpecification(
                        SecurityUtils.getUserFromJwtToken(jwt, authentication),
                        orderSearchDto
                ),
                RequestUtils.getSortAndPageRequest(orderSearchDto)
        );
        return ResponseUtils.createPaginatedResponse(result);
    }

    /**
     * Get counters for Order (active and completed)
     *
     * @param orderSearchDto Order search DTO
     * @param jwt            oauth2 resource server Jwt token
     * @param authentication Spring security authentication
     * @return ResponseEntity with status 200 OK and single payload of order counters response DTO
     */

    @GetMapping("/counters")
    public ResponseEntity<SinglePayloadResponseDto<OrderCountersResponseDto>> getAllOrdersCounters(
            @Valid OrderSearchDto orderSearchDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        OrderCountersResponseDto result = orderService.getCounters(orderSearchDto, SecurityUtils.getUserFromJwtToken(
                jwt, authentication
        ));
        return ResponseUtils.createSingleResponse(result);
    }

    /**
     * Service endpoint for collect midnight scheduled job stats
     * Accessible only for service role
     *
     * @return ResponseEntity with status 200 OK and single payload of order stats report response DTO
     */

    @GetMapping("collect_stats")
    public ResponseEntity<SinglePayloadResponseDto<OrderStatsReportResponseDto>> collectStats() {
        return ResponseUtils.createSingleResponse(orderService.collectStats());
    }

    /**
     * Endpoint for create new Order
     *
     * @param orderCreateDto Order create DTO
     * @param jwt            oauth2 resource server Jwt token
     * @param authentication Spring security authentication
     * @param uriBuilder     UriComponentsBuilder - autowired for building 201 Created location redirect
     * @return ResponseEntity with status 201 Created and single payload with newly created Order entity
     */

    @PostMapping
    public ResponseEntity<SinglePayloadResponseDto<OrderResponseDto>> createOrder(
            @Valid @RequestBody OrderCreateDto orderCreateDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication,
            UriComponentsBuilder uriBuilder
    ) {
        OrderResponseDto result = orderService.create(
                orderCreateDto,
                SecurityUtils.getUserFromJwtToken(jwt, authentication)
        );
        return ResponseEntity.created(uriBuilder.replacePath("/api/v1/orders/{orderId}")
                        .build(Map.of("orderId", result.getId())))
                .body(SinglePayloadResponseDto.<OrderResponseDto>builder()
                        .code(HttpStatus.CREATED.value())
                        .message(HttpStatus.CREATED.getReasonPhrase())
                        .payload(result)
                        .build());
    }
}
