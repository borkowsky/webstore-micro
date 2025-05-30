package net.rewerk.webstore.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * User DTO object
 *
 * @author rewerk
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * User Role enum
     */

    public enum Role {
        ROLE_USER,
        ROLE_MANAGER,
        ROLE_ADMIN,
        ROLE_SERVICE
    }

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserDto.Role role;
}
