package com.eduardocastro.order_service.domain.event;

import com.eduardocastro.order_service.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
    UUID aggregateId,
    String userId,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime occurredAt
) implements DomainEvent {}
