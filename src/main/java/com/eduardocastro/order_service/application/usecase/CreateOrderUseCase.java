package com.eduardocastro.order_service.application.usecase;

import com.eduardocastro.order_service.application.dto.OrderItemInput;
import com.eduardocastro.order_service.domain.entity.Order;

import java.util.List;

public interface CreateOrderUseCase {
    Order execute(String userId, List<OrderItemInput> items);
}
