package net.rewerk.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.request.basket.BasketPatchDto;
import net.rewerk.users.service.entity.BasketService;
import net.rewerk.webstore.entity.Basket;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Parametrized REST controller for Basket service
 *
 * @author rewerk
 */

@RequestMapping("/api/v1/basket/{id:\\d+}")
@RestController
@RequiredArgsConstructor
public class BasketController {
    private final BasketService basketService;

    /**
     * Method for populate basket method attribute by Basket identifier request mapping path variable
     *
     * @param id             Basket identifier from path variable
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Response with status 200 OK and payload with single Basket entity response DTO
     */

    @ModelAttribute("basket")
    public Basket getBasket(@PathVariable Integer id,
                            @AuthenticationPrincipal Jwt jwt,
                            Authentication authentication) {
        return basketService.findBasketById(id, SecurityUtils.getUserFromJwtToken(jwt, authentication));
    }

    /**
     * PATCH endpoint for update Basket entity
     *
     * @param currentBasket  Basket to update, retrieved from model attribute
     * @param basketPatchDto DTO with patch data
     * @param jwt            OAuth2 jwt token
     * @return Response with status 204 No Content without payload
     */

    @PatchMapping
    public ResponseEntity<Void> updateBasket(
            @ModelAttribute("basket") Basket currentBasket,
            @Valid @RequestBody BasketPatchDto basketPatchDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        basketService.update(currentBasket, basketPatchDto, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE endpoint for delete Basket entity
     *
     * @param basket Basket entity to delete, retrieved from model attribute
     * @param jwt    OAuth2 jwt token
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteBasket(
            @ModelAttribute("basket") Basket basket,
            @AuthenticationPrincipal Jwt jwt
    ) {
        basketService.delete(basket, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }
}
