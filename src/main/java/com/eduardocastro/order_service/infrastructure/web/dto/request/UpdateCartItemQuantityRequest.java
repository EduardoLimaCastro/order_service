package com.eduardocastro.order_service.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateCartItemQuantityRequest(@Min(1) int quantity) {}
