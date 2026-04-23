package com.eduardocastro.order_service.application.usecase;

import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.enums.OrderStatus;

import java.util.UUID;

public interface UpdateOrderStatusUseCase {
    Order execute(UUID id, OrderStatus newStatus);
}
