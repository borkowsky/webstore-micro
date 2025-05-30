package net.rewerk.webstore.products.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
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
 * Main security configuration for products microservice
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
                .authorizeHttpRequests(
                        customizer -> customizer
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/v1/products/**",
                                        "/api/v1/categories/**",
                                        "/api/v1/brands/**"
                                )
                                .permitAll()
                                .requestMatchers("/api/v1/stats")
                                .hasRole("ADMIN")
                                .requestMatchers("/api/v1/products/by_ids")
                                .hasRole("SERVICE")
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/products")
                                .hasRole("SERVICE")
                                .anyRequest()
                                .hasAnyRole("ADMIN", "SERVICE")
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
