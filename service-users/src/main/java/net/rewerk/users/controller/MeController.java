package net.rewerk.users.controller;

import lombok.RequiredArgsConstructor;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.me.MeResponseDto;
import net.rewerk.users.util.KeycloakClient;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for retrieve authenticated user information
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class MeController {
    @Value("${keycloak_admin.realm}")
    private String realm;
    private final KeycloakClient keycloakClient;

    /**
     * GET endpoint for retrieve current authenticated user details
     *
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Response with status 200 and payload with single user details response DTO
     */

    @GetMapping("me")
    public ResponseEntity<SinglePayloadResponseDto<MeResponseDto>> me(
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Optional<? extends GrantedAuthority> role = authentication.getAuthorities().stream()
                .filter(authority -> authority.getAuthority().startsWith("ROLE_"))
                .findFirst();
        return ResponseEntity.ok().body(SinglePayloadResponseDto.<MeResponseDto>builder()
                .code(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .payload(MeResponseDto.builder()
                        .id(UUID.fromString(jwt.getSubject()))
                        .username(jwt.getClaimAsString(StandardClaimNames.PREFERRED_USERNAME))
                        .email(jwt.getClaimAsString(StandardClaimNames.EMAIL))
                        .firstName(jwt.getClaimAsString(StandardClaimNames.GIVEN_NAME))
                        .lastName(jwt.getClaimAsString(StandardClaimNames.FAMILY_NAME))
                        .role(role.map(GrantedAuthority::getAuthority).orElse(null))
                        .build())
                .build());
    }

    /**
     * GET endpoint for provide close Keycloak's authenticated user session
     *
     * @param jwt OAuth2 jwt token
     * @return Response with status 204 No Content without payload
     */

    @PostMapping("logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Keycloak client = keycloakClient.getClient();
        UserResource resource = client.realm(realm).users().get(jwt.getSubject());
        if (resource != null) {
            resource.logout();
        }
        return ResponseEntity.noContent().build();
    }
}
