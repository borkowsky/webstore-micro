package net.rewerk.webstore.dto.response.me;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeResponseDto {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
