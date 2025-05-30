package net.rewerk.webstore.utility;

import net.rewerk.webstore.dto.entity.UserDto;
import net.rewerk.webstore.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Common security utilities
 *
 * @author rewerk
 */

public abstract class SecurityUtils {

    /**
     * Form user information DTO from OAuth2 jwt token and Spring Security Authentication object
     *
     * @param token          OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Formed User information
     */

    public static User getUserFromJwtToken(Jwt token, Authentication authentication) {
        if (token == null || authentication == null) {
            return null;
        }
        Optional<? extends GrantedAuthority> role = authentication.getAuthorities().stream()
                .filter(authority -> authority.getAuthority().startsWith("ROLE_"))
                .findFirst();
        return User.builder()
                .id(UUID.fromString(token.getSubject()))
                .username(token.getClaimAsString(StandardClaimNames.PREFERRED_USERNAME))
                .email(token.getClaimAsString(StandardClaimNames.EMAIL))
                .firstName(token.getClaimAsString(StandardClaimNames.GIVEN_NAME))
                .lastName(token.getClaimAsString(StandardClaimNames.FAMILY_NAME))
                .role(User.Role.valueOf(role.map(GrantedAuthority::getAuthority).orElse("ROLE_USER")))
                .build();
    }

    /**
     * Form user information response DTO from OAuth2 jwt token and Spring Security Authentication object
     *
     * @param token          OAuth2 jwt token
     * @param authentication Spring Security Authentication object
     * @return Formed User information response DTO
     */

    public static UserDto getUserDtoFromJwtToken(Jwt token, Authentication authentication) {
        if (token == null || authentication == null) {
            return null;
        }
        Optional<? extends GrantedAuthority> role = authentication.getAuthorities().stream()
                .filter(authority -> authority.getAuthority().startsWith("ROLE_"))
                .findFirst();
        return UserDto.builder()
                .id(UUID.fromString(token.getSubject()))
                .username(token.getClaimAsString(StandardClaimNames.PREFERRED_USERNAME))
                .email(token.getClaimAsString(StandardClaimNames.EMAIL))
                .firstName(token.getClaimAsString(StandardClaimNames.GIVEN_NAME))
                .lastName(token.getClaimAsString(StandardClaimNames.FAMILY_NAME))
                .role(UserDto.Role.valueOf(role.map(GrantedAuthority::getAuthority).orElse("ROLE_USER")))
                .build();
    }

    /**
     * Retrieve collection of GrantedAuthority from OAuth2 jwt token
     *
     * @param jwt OAuth2 jwt token
     * @return Collection of GrantedAuthority
     */

    public static Collection<GrantedAuthority> getAuthoritiesFromJwt(Jwt jwt) {
        if (jwt == null) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Object> claims = jwt.getClaims();
        if (claims.containsKey("realm_access")
                && claims.get("realm_access") instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
            if (realmAccess.containsKey("roles") && realmAccess.get("roles") instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                authorities = roles.stream()
                        .filter(s -> s.startsWith("ROLE_"))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
        }
        return authorities;
    }
}
