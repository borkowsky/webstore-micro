package net.rewerk.webstore.reviews.configuration;

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
 * Main security configuration for reviews microservice
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
                                .requestMatchers(HttpMethod.GET)
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/reviews/probe")
                                .authenticated()
                                .requestMatchers(
                                        HttpMethod.POST, "/api/v1/reviews"
                                )
                                .authenticated()
                                .requestMatchers(
                                        "/api/v1/reviews/collect_stats"
                                )
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
