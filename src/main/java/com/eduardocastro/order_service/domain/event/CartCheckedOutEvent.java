package com.eduardocastro.order_service.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CartCheckedOutEvent(
    UUID aggregateId,
    String userId,
    UUID orderId,
    LocalDateTime occurredAt
) implements DomainEvent {}
