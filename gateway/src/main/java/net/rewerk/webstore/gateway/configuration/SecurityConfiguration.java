package net.rewerk.webstore.gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.*;
import org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableRedisWebSession
public class SecurityConfiguration {
    @Value("${frontend.uri}")
    private String FRONTEND_URI;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
                                                  ServerOAuth2AuthorizationRequestResolver resolver,
                                                  ServerOAuth2AuthorizedClientRepository auth2AuthorizedClientRepository,
                                                  ServerLogoutSuccessHandler logoutSuccessHandler,
                                                  ServerLogoutHandler logoutHandler) {
        return httpSecurity
                .cors(cors -> cors.configurationSource(_ -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setAllowedOrigins(List.of(FRONTEND_URI));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of(
                            "POST", "GET", "OPTIONS", "DELETE", "HEAD", "PATCH", "TRACE"
                    ));
                    return corsConfiguration;
                }))
                .authorizeExchange(
                        authorizeExchange ->
                                authorizeExchange
                                        .pathMatchers(HttpMethod.HEAD)
                                        .permitAll()
                                        .pathMatchers(HttpMethod.OPTIONS)
                                        .permitAll()
                                        .pathMatchers(
                                                "/actuator/**",
                                                "/api/v1/users/**",
                                                "/api/v1/categories/**",
                                                "/api/v1/products/**",
                                                "/api/v1/brands/**",
                                                "/api/v1/search/**",
                                                "/api/v1/basket/**",
                                                "/api/v1/addresses/**",
                                                "/api/v1/reviews/**",
                                                "/api/v1/orders/**",
                                                "/api/v1/favorites/**",
                                                "/api/v1/payments/**",
                                                "/api/v1/uploads/**",
                                                "/api/v1/events/**",
                                                "/api/v1/stats/**"
                                        )
                                        .permitAll()
                                        .pathMatchers(
                                                "/auth/login"
                                        )
                                        .authenticated()
                                        .anyExchange()
                                        .denyAll()
                ).oauth2Login(oauth2Login -> oauth2Login
                        .authorizationRequestResolver(resolver)
                        .authorizedClientRepository(auth2AuthorizedClientRepository)
                )
                .logout(customizer -> customizer
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .logoutHandler(logoutHandler)
                )
//                .logout(logout -> logout
//                        .logoutSuccessHandler(logoutSuccessHandler)
//                        .logoutHandler(logoutHandler)
//                )
//                .csrf(csrf -> {
//                    ServerCsrfTokenRequestHandler delegate = new XorServerCsrfTokenRequestAttributeHandler();
//                    csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//                            .csrfTokenRequestHandler(delegate::handle);
//                })
//                .csrf(csrf -> {
//                    csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse());
//                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    ServerOAuth2AuthorizationRequestResolver requestResolver(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var resolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    ServerLogoutSuccessHandler logoutSuccessHandler(
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {
        OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri(FRONTEND_URI);
        return oidcLogoutSuccessHandler;
    }

    @Bean
    ServerLogoutHandler logoutHandler() {
        return new DelegatingServerLogoutHandler(
                new SecurityContextServerLogoutHandler(),
                new WebSessionServerLogoutHandler(),
                new HeaderWriterServerLogoutHandler(
                        new ClearSiteDataServerHttpHeadersWriter(ClearSiteDataServerHttpHeadersWriter.Directive.COOKIES)
                )
        );
    }

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        final String AUTH_COOKIE_NAME = "gateway-auth";
        resolver.setCookieName(AUTH_COOKIE_NAME);
        resolver.addCookieInitializer((builder) -> builder.path("/"));
        resolver.addCookieInitializer((builder) -> builder.sameSite("Lax"));
        resolver.addCookieInitializer((builder) -> builder
                .maxAge(60 * 60 * 24));
        return resolver;
    }
}
