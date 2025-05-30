package net.rewerk.webstore.orders.controller;

import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.entity.Order;
import net.rewerk.webstore.dto.request.order.OrderPatchDto;
import net.rewerk.webstore.dto.response.order.OrderResponseDto;
import net.rewerk.webstore.orders.service.aggregator.OrderAggregatorService;
import net.rewerk.webstore.orders.service.entity.OrderService;
import net.rewerk.webstore.utility.ResponseUtils;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Order service parametrized REST controller
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders/{id:\\d+}")
public class OrderController {
    private final OrderService orderService;
    private final OrderAggregatorService orderAggregatorService;

    /**
     * Model attribute order
     *
     * @param id             PathVariable id - order id
     * @param jwt            OAuth2 resource server jwt token
     * @param authentication Spring security authentication
     * @return Order entity
     */

    @ModelAttribute("order")
    public Order getOrder(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        return orderService.findById(
                id,
                SecurityUtils.getUserFromJwtToken(jwt, authentication)
        );
    }

    /**
     * GET endpoint for get Order
     *
     * @param order Model attribute - order
     * @return Response with status 200 - OK and single payload with Order entity response DTO
     */

    @GetMapping
    public ResponseEntity<SinglePayloadResponseDto<OrderResponseDto>> getOrder(
            @ModelAttribute Order order
    ) {
        return ResponseUtils.createSingleResponse(
                orderAggregatorService.aggregate(order)
        );
    }

    /**
     * Patch Order by id
     *
     * @param order         Model attribute - order
     * @param orderPatchDto Order path DTO
     * @return Response with status 204 - No content without payload
     */

    @PatchMapping
    public ResponseEntity<Void> updateOrder(
            @ModelAttribute("order") Order order,
            @RequestBody OrderPatchDto orderPatchDto
    ) {
        orderService.update(order, orderPatchDto);
        return ResponseEntity.noContent().build();
    }
}
