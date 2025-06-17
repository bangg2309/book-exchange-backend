package com.bookexchange.mapper;

import com.bookexchange.dto.request.UserCreationRequest;
import com.bookexchange.dto.request.UserUpdateRequest;
import com.bookexchange.dto.response.RoleResponse;
import com.bookexchange.dto.response.UserResponse;
import com.bookexchange.entity.Role;
import com.bookexchange.entity.User;
import com.bookexchange.exception.AppException;
import com.bookexchange.exception.ErrorCode;
import com.bookexchange.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    public User toUser(UserCreationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus());
        return user;
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
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
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus());

    }
}
