package com.test.demoproductkumamesh.model.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record IdListRequest(
        @NotNull(message = "ids is required")
        List<UUID> ids
) {}
