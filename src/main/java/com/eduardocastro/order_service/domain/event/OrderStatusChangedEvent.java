package com.eduardocastro.order_service.domain.event;

import com.eduardocastro.order_service.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderStatusChangedEvent(
    UUID aggregateId,
    OrderStatus previousStatus,
    OrderStatus newStatus,
    LocalDateTime occurredAt
) implements DomainEvent {}
