package com.eduardocastro.order_service.application.usecase;

import com.eduardocastro.order_service.application.dto.OrderItemInput;
import com.eduardocastro.order_service.domain.entity.Cart;

public interface AddCartItemUseCase {
    Cart execute(String userId, OrderItemInput item);
}
