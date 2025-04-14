package com.bookexchange.mapper;

import com.bookexchange.dto.request.UserCreationRequest;
import com.bookexchange.dto.request.UserUpdateRequest;
import com.bookexchange.dto.response.RoleResponse;
import com.bookexchange.dto.response.UserResponse;
import com.bookexchange.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleMapper roleMapper;

    public User toUser(UserCreationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        if (user.getRoles() != null) {
            Set<RoleResponse> roles = user.getRoles().stream()
                    .map(roleMapper::toRoleResponse)
                    .collect(Collectors.toSet());
            response.setRoles(roles);
        } else {
            response.setRoles(Collections.emptySet());
        }
        return response;
    }

    public void updateUser(User user, UserUpdateRequest request) {
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
    }
}
