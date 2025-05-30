package net.rewerk.webstore.reviews.feign.client;

import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.reviews.feign.client.fallback.UsersFeignClientFallback;
import net.rewerk.webstore.reviews.feign.configuration.DefaultConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for retrieve users information
 *
 * @author rewerk
 */

@FeignClient(
        name = "users-client",
        url = "${services.users.base_uri}",
        configuration = {
                DefaultConfiguration.class
        },
        fallback = UsersFeignClientFallback.class
)
public interface UsersFeignClient {

    /**
     * Cacheable method for retrieve user information by identifier
     *
     * @param userId User identifier
     * @return Response with single payload with User entity response DTO
     */

    @Cacheable(
            value = "caffeine",
            key = "'users-client-getById' + #userId"
    )
    @GetMapping
    SinglePayloadResponseDto<UserResponseDto> getById(@RequestParam(name = "user_id") UUID userId);

    /**
     * Cacheable method for retrieve multiple user information by list of identifiers
     *
     * @param userIds List of User identifiers
     * @return Response with payload with collection of User entity response DTO
     */

    @Cacheable(
            value = "caffeine",
            key = "'users-client-getByIds' + #userIds"
    )
    @GetMapping("by_ids")
    PayloadResponseDto<UserResponseDto> getByIds(@RequestParam(name = "user_id") List<UUID> userIds);
}
