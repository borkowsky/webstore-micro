package net.rewerk.webstore.orders.configuration;

import lombok.NonNull;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class JwtGrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        return SecurityUtils.getAuthoritiesFromJwt(jwt);
    }
}