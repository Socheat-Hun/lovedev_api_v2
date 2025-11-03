package com.lovedev.user.mapper;

import com.lovedev.user.model.dto.request.CreatePermissionRequest;
import com.lovedev.user.model.dto.response.PermissionResponse;
import com.lovedev.user.model.entity.Permission;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PermissionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Permission toEntity(CreatePermissionRequest request);

    @Mapping(target = "roles", expression = "java(permission.getRoleNames())")
    PermissionResponse toResponse(Permission permission);

    List<PermissionResponse> toResponseList(List<Permission> permissions);
}