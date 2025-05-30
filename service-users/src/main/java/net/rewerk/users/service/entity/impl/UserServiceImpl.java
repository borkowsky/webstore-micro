package net.rewerk.users.service.entity.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.users.service.entity.UserService;
import net.rewerk.users.util.KeycloakClient;
import net.rewerk.webstore.dto.request.user.UserSearchDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Users service implementation
 *
 * @author rewerk
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${keycloak_admin.realm}")
    private String realm;
    private final KeycloakClient keycloakClient;

    /**
     * Get user information by identifier
     *
     * @param id User identifier to search
     * @return User response DTO
     */

    @Override
    public UserResponseDto getById(UUID id) {
        log.info("UserServiceImpl.getById: Getting user by id: {}", id);
        Keycloak client = keycloakClient.getClient();
        UserResource resource = client.realm(realm).users().get(id.toString());
        if (resource == null) {
            return null;
        }
        UserRepresentation representation = resource.toRepresentation();
        return UserResponseDto.builder()
                .id(UUID.fromString(representation.getId()))
                .username(representation.getUsername())
                .build();
    }

    /**
     * Get list of users by identifiers
     *
     * @param ids List of users identifiers
     * @return List of users response DTO
     */

    @Override
    public List<UserResponseDto> getByIds(List<UUID> ids) {
        log.info("UserServiceImpl.getByIds: Getting list of users by list of ids: {}", ids);
        return ids.stream()
                .map(this::getById)
                .toList();
    }

    /**
     * Search users by username
     *
     * @param searchDto DTO with search parameters
     * @return List of users response DTO
     */

    @Override
    public List<UserResponseDto> searchByUsername(UserSearchDto searchDto) {
        log.info("UserServiceImpl.searchByUsername: Getting list of users by searchDto: {}", searchDto);
        Keycloak client = keycloakClient.getClient();
        List<UserRepresentation> users = client.realm(realm).users().search(searchDto.getUsername());
        return users.stream()
                .filter(u -> !u.getUsername().contains("service-account"))
                .map(user -> UserResponseDto.builder()
                        .username(user.getUsername())
                        .id(UUID.fromString(user.getId()))
                        .build())
                .toList();
    }
}
