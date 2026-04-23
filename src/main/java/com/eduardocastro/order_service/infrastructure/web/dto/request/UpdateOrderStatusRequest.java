package com.eduardocastro.order_service.infrastructure.web.dto.request;

import com.eduardocastro.order_service.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
