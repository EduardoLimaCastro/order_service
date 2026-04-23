package com.eduardocastro.order_service.application.dto;

import java.math.BigDecimal;

public record OrderItemInput(
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice
) {}
