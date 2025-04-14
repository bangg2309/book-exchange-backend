package com.bookexchange.mapper;

import com.bookexchange.dto.request.PermissionRequest;
import com.bookexchange.dto.response.PermissionResponse;
import com.bookexchange.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionResponse toPermissionResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setName(permission.getName());
        response.setDescription(permission.getDescription());
        return response;
    }

    public Permission toPermission(PermissionRequest request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        return permission;
    }
}

