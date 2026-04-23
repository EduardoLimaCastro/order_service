package com.eduardocastro.order_service.domain.repository;

import com.eduardocastro.order_service.domain.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByUserId(String userId);
}
