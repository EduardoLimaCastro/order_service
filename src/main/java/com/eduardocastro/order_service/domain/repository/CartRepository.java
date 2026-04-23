package com.eduardocastro.order_service.domain.repository;

import com.eduardocastro.order_service.domain.entity.Cart;

import java.util.Optional;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findByUserId(String userId);
    void deleteByUserId(String userId);
}
