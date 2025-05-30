package net.rewerk.users.util;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for create Keycloak admin client
 *
 * @author rewerk
 */

@Component
@Slf4j
public class KeycloakClient {
    private final Keycloak keycloak;

    /**
     * Constructor
     * Creates Keycloak admin client instance
     *
     * @param username Keycloak admin username
     * @param password Keycloak admin password
     * @param uri      Keycloak admin URI
     */

    public KeycloakClient(
            @Value("${keycloak_admin.username}")
            String username,
            @Value("${keycloak_admin.password}")
            String password,
            @Value("${keycloak_admin.uri}")
            String uri
    ) {
        log.info("KeycloakClient: Creating Keycloak instance...");
        final String REALM = "master";
        final String CLIENT_ID = "admin-cli";
        keycloak = KeycloakBuilder.builder()
                .serverUrl(uri)
                .realm(REALM)
                .username(username)
                .password(password)
                .clientId(CLIENT_ID)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
        log.info("KeycloakClient: Keycloak instance created.");
    }

    /**
     * Get instance of Keycloak admin client
     *
     * @return Instance of Keycloak admin client
     */

    public Keycloak getClient() {
        log.info("KeycloakClient: Getting Keycloak instance");
        return keycloak;
    }

    /**
     * Graceful close Keycloak admin client
     */

    @PreDestroy
    public void close() {
        log.info("KeycloakClient: Closing Keycloak instance...");
        keycloak.close();
        log.info("KeycloakClient: Keycloak instance closed.");
    }
}
