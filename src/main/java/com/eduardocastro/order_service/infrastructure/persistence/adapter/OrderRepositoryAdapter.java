package com.eduardocastro.order_service.infrastructure.persistence.adapter;

import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.repository.OrderRepository;
import com.eduardocastro.order_service.infrastructure.persistence.jpa.OrderJpaRepository;
import com.eduardocastro.order_service.infrastructure.persistence.mapper.OrderJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return OrderJpaMapper.toDomain(
            orderJpaRepository.save(OrderJpaMapper.toEntity(order))
        );
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderJpaRepository.findById(id)
            .map(OrderJpaMapper::toDomain);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return orderJpaRepository.findByUserId(userId).stream()
            .map(OrderJpaMapper::toDomain)
            .toList();
    }
}
