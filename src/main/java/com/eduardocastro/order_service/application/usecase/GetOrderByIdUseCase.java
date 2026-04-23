package com.eduardocastro.order_service.application.usecase;

import com.eduardocastro.order_service.domain.entity.Order;

import java.util.Optional;
import java.util.UUID;

public interface GetOrderByIdUseCase {
    Optional<Order> execute(UUID id);
}
