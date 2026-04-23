package com.eduardocastro.order_service.infrastructure.persistence.adapter;

import com.eduardocastro.order_service.domain.entity.Cart;
import com.eduardocastro.order_service.domain.repository.CartRepository;
import com.eduardocastro.order_service.infrastructure.persistence.jpa.CartJpaRepository;
import com.eduardocastro.order_service.infrastructure.persistence.mapper.CartJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepository {

    private final CartJpaRepository cartJpaRepository;

    @Override
    public Cart save(Cart cart) {
        return CartJpaMapper.toDomain(
            cartJpaRepository.save(CartJpaMapper.toEntity(cart))
        );
    }

    @Override
    public Optional<Cart> findByUserId(String userId) {
        return cartJpaRepository.findByUserId(userId)
            .map(CartJpaMapper::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        cartJpaRepository.deleteByUserId(userId);
    }
}
