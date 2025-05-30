package net.rewerk.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.users.service.entity.UserService;
import net.rewerk.webstore.dto.request.user.UserSearchDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;
import net.rewerk.webstore.utility.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for retrieve users details
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    private final UserService userService;

    /**
     * GET endpoint for retrieve user information by identifier
     *
     * @param userId User identifier
     * @return Response with status 200 OK and payload with single User response DTO
     */

    @GetMapping
    public ResponseEntity<SinglePayloadResponseDto<UserResponseDto>> getUserById(
            @RequestParam(name = "user_id") UUID userId
    ) {
        return ResponseUtils.createSingleResponse(userService.getById(userId));
    }

    /**
     * GET endpoint for retrieve multiple users information by list of identifiers
     *
     * @param userIds List of user identifiers
     * @return Response with status 200 OK and payload with collection of user response DTO
     */

    @GetMapping("by_ids")
    public ResponseEntity<PayloadResponseDto<UserResponseDto>> getUsersByIds(
            @RequestParam(name = "user_id") List<UUID> userIds
    ) {
        return ResponseUtils.createCollectionResponse(userService.getByIds(userIds));
    }

    /**
     * GET endpoint for provide search users information by parameters
     *
     * @param searchDto DTO with search parameters
     * @return Response with status 200 OK and payload with collection of user information DTO
     */

    @GetMapping("/search")
    public ResponseEntity<PayloadResponseDto<UserResponseDto>> searchUsers(
            @Valid UserSearchDto searchDto
    ) {
        List<UserResponseDto> result = userService.searchByUsername(searchDto);
        return ResponseUtils.createCollectionResponse(result);
    }
}
