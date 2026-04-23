package com.eduardocastro.order_service.infrastructure.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
    UUID id,
    String userId,
    BigDecimal totalAmount,
    List<CartItemResponse> items,
    String createdAt,
    String updatedAt
) {}
