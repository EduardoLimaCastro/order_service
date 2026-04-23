package com.eduardocastro.order_service.application.usecase;

import com.eduardocastro.order_service.domain.entity.Cart;

import java.util.Optional;

public interface GetCartByUserUseCase {
    Optional<Cart> execute(String userId);
}
