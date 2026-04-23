package com.eduardocastro.order_service.application.usecase;

import com.eduardocastro.order_service.domain.entity.Cart;

import java.util.UUID;

public interface RemoveCartItemUseCase {
    Cart execute(String userId, UUID itemId);
}
