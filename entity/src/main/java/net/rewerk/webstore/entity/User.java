package net.rewerk.webstore.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
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
    private Role role;
}
