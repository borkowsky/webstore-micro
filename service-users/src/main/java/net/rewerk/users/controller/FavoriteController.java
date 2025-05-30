package net.rewerk.users.controller;

import lombok.RequiredArgsConstructor;
import net.rewerk.users.service.entity.FavoriteService;
import net.rewerk.webstore.entity.Favorite;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Parametrized REST controller for Favorite service
 *
 * @author rewerk
 */

@RequestMapping("/api/v1/favorites/{id:\\d+}")
@RestController
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    /**
     * Method for populate favorite method attribute by Favorite identifier request mapping path variable
     *
     * @param id             Favorite identifier
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Favorite entity
     */

    @ModelAttribute("favorite")
    public Favorite getFavorite(@PathVariable Integer id,
                                @AuthenticationPrincipal Jwt jwt,
                                Authentication authentication
    ) {
        return favoriteService.findById(id, SecurityUtils.getUserFromJwtToken(jwt, authentication));
    }

    /**
     * DELETE endpoint for delete Favorite entity
     *
     * @param favorite Favorite entity for delete, retrieved from model attribute
     * @param jwt      OAuth2 jwt token
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteFavorite(
            @ModelAttribute Favorite favorite,
            @AuthenticationPrincipal Jwt jwt
    ) {
        favoriteService.delete(favorite, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }
}
