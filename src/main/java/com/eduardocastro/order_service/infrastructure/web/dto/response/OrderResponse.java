package com.eduardocastro.order_service.infrastructure.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    String userId,
    BigDecimal totalAmount,
    String status,
    List<OrderItemResponse> items,
    String createdAt,
    String updatedAt
) {}
