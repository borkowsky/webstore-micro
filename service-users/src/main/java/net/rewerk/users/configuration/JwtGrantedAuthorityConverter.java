package net.rewerk.users.configuration;

import lombok.NonNull;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * JWT Granted authority converter component
 *
 * @author rewerk
 */

@Component
public class JwtGrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    /**
     * @param jwt the source object to convert, which must be an instance of {@code S} (never {@code null}) Jwt token to retrieve authorities
     * @return Collection of GrantedAuthority
     */

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        return SecurityUtils.getAuthoritiesFromJwt(jwt);
    }
}