package net.rewerk.webstore.events.feign.client.fallback;

import jakarta.persistence.EntityNotFoundException;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.events.feign.client.UsersFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Fallback class for UsersFeignClient
 *
 * @author rewerk
 */

@Component
public class UsersFeignClientFallback implements UsersFeignClient {
    @Override
    public SinglePayloadResponseDto<UserResponseDto> getById(UUID uuid) {
        throw new EntityNotFoundException("User with id %s not found".formatted(uuid.toString()));
    }

    @Override
    public PayloadResponseDto<UserResponseDto> getByIds(List<UUID> uuids) {
        throw new EntityNotFoundException("User with ids in %s was not found"
                .formatted(String.join(", ", uuids.stream().map(UUID::toString).toArray(String[]::new))));
    }
}
