package net.rewerk.webstore.events.feign.client;

import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.events.feign.client.fallback.UsersFeignClientFallback;
import net.rewerk.webstore.events.feign.configuration.DefaultConfiguration;
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
     * Cacheable method for retrieve user information by user identifier
     *
     * @param userId User identifier
     * @return SinglePayloadResponseDto with UserResponseDto
     */

    @Cacheable(
            value = "caffeine",
            key = "'users-client-getById' + #userId"
    )
    @GetMapping
    SinglePayloadResponseDto<UserResponseDto> getById(@RequestParam(name = "user_id") UUID userId);

    /**
     * Cacheable method for retrieve users information by list of identifiers
     *
     * @param userIds List of user identifiers
     * @return PayloadResponseDto with collection of UserResponseDto
     */

    @Cacheable(
            value = "caffeine",
            key = "'users-client-getByIds' + #userIds"
    )
    @GetMapping("by_ids")
    PayloadResponseDto<UserResponseDto> getByIds(@RequestParam(name = "user_id") List<UUID> userIds);
}
