package com.eduardocastro.order_service.application.usecase;

import com.eduardocastro.order_service.domain.entity.Order;

import java.util.List;

public interface ListOrdersByUserUseCase {
    List<Order> execute(String userId);
}
