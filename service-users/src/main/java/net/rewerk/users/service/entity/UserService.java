package net.rewerk.users.service.entity;

import net.rewerk.webstore.dto.request.user.UserSearchDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto getById(UUID id);

    List<UserResponseDto> getByIds(List<UUID> ids);

    List<UserResponseDto> searchByUsername(UserSearchDto searchDto);
}
