package com.eduardocastro.order_service.infrastructure.persistence.mapper;

import com.eduardocastro.order_service.domain.entity.Cart;
import com.eduardocastro.order_service.domain.entity.CartItem;
import com.eduardocastro.order_service.infrastructure.persistence.jpa.CartItemJpaEntity;
import com.eduardocastro.order_service.infrastructure.persistence.jpa.CartJpaEntity;

import java.util.List;

public class CartJpaMapper {

    private CartJpaMapper() {}

    public static CartJpaEntity toEntity(Cart cart) {
        CartJpaEntity entity = new CartJpaEntity(
            cart.getId(),
            cart.getUserId(),
            cart.getTotalAmount(),
            cart.getCreatedAt(),
            cart.getUpdatedAt()
        );
        cart.getItems().stream()
            .map(item -> toItemEntity(item, entity))
            .forEach(entity.getItems()::add);
        return entity;
    }

    public static Cart toDomain(CartJpaEntity entity) {
        List<CartItem> items = entity.getItems().stream()
            .map(CartJpaMapper::toItemDomain)
            .toList();
        return Cart.reconstitute(
            entity.getId(),
            entity.getUserId(),
            entity.getTotalAmount(),
            items,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    private static CartItemJpaEntity toItemEntity(CartItem item, CartJpaEntity cartEntity) {
        return new CartItemJpaEntity(
            item.getId(),
            cartEntity,
            item.getProductId(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getSubtotal()
        );
    }

    private static CartItem toItemDomain(CartItemJpaEntity entity) {
        return CartItem.reconstitute(
            entity.getId(),
            entity.getProductId(),
            entity.getProductName(),
            entity.getQuantity(),
            entity.getUnitPrice(),
            entity.getSubtotal()
        );
    }
}
