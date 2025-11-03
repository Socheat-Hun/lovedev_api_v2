package com.lovedev.user.model.dto.request;

import com.lovedev.user.model.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotNull(message = "Status is required")
    private UserStatus status;
}