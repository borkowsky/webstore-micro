package net.rewerk.users.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.oauth2.core.oidc.StandardClaimNames.PREFERRED_USERNAME;

/**
 * Main security configuration for uploads microservice
 *
 * @author rewerk
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    /**
     * Main security filter chain bean
     *
     * @param http HttpSecurity object - autowired
     * @return Configured HttpSecurity object
     * @throws Exception - if security chain fails
     */

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/api/v1/users",
                                "/api/v1/addresses/by_ids",
                                "/api/v1/basket/by_ids"
                        )
                        .hasRole("SERVICE")
                        .requestMatchers("/api/v1/users/search")
                        .hasAnyRole("ADMIN", "MANAGER")
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter())))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    /**
     * Main authentication converter for retrieve data from JWT token
     *
     * @return Jwt authentication converter
     */

    private Converter<Jwt, AbstractAuthenticationToken> authenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthorityConverter());
        jwtAuthenticationConverter.setPrincipalClaimName(PREFERRED_USERNAME);
        return jwtAuthenticationConverter;
    }
}
