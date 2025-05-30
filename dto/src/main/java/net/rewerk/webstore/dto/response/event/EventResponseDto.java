package net.rewerk.webstore.dto.response.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.response.user.UserResponseDto;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {
    private Integer id;
    @JsonIgnore
    private UUID userId;
    private UserResponseDto user;
    private String text;
    private Date createdAt;
    private Date updatedAt;
}
