package com.bookexchange.mapper;

import com.bookexchange.dto.request.RoleRequest;
import com.bookexchange.dto.response.PermissionResponse;
import com.bookexchange.dto.response.RoleResponse;
import com.bookexchange.entity.Permission;
import com.bookexchange.entity.Role;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public Role toRole(RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());

        if (request.getPermissions() != null) {
            Set<Permission> permissions = request.getPermissions().stream()
                    .map(name -> {
                        Permission permission = new Permission();
                        permission.setName(name);
                        return permission;
                    })
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        } else {
            role.setPermissions(Collections.emptySet());
        }

        return role;
    }

    public RoleResponse toRoleResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setName(role.getName());
        response.setDescription(role.getDescription());

        if (role.getPermissions() != null) {
            Set<PermissionResponse> permissions = role.getPermissions().stream()
                    .map(permission -> {
                        PermissionResponse permissionResponse = new PermissionResponse();
                        permissionResponse.setName(permission.getName());
                        permissionResponse.setDescription(permission.getDescription());
                        return permissionResponse;
                    })
                    .collect(Collectors.toSet());
            response.setPermissions(permissions);
        } else {
            response.setPermissions(Collections.emptySet());
        }

        return response;
    }
}
