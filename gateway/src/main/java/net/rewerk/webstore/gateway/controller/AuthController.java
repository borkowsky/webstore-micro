package net.rewerk.webstore.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Value("${frontend.uri}")
    private String FRONTEND_URI;

    @GetMapping("login")
    public Mono<ResponseEntity<Void>> login(
            Authentication authentication
            ) {
        if (authentication != null && authentication.isAuthenticated()) {
            return Mono.just(ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .location(URI.create(FRONTEND_URI))
                    .build());
        } else {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
    }

    @PostMapping("logout")
    public Mono<ResponseEntity<Void>> logout() {
        return Mono.just(ResponseEntity.noContent().build());
    }
}
