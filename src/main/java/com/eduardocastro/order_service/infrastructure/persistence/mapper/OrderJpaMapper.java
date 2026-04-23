package com.eduardocastro.order_service.infrastructure.persistence.mapper;

import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.entity.OrderItem;
import com.eduardocastro.order_service.infrastructure.persistence.jpa.OrderItemJpaEntity;
import com.eduardocastro.order_service.infrastructure.persistence.jpa.OrderJpaEntity;

import java.util.List;

public class OrderJpaMapper {

    private OrderJpaMapper() {}

    public static OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
        order.getItems().stream()
            .map(item -> toItemEntity(item, entity))
            .forEach(entity.getItems()::add);
        return entity;
    }

    public static Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
            .map(OrderJpaMapper::toItemDomain)
            .toList();
        return Order.reconstitute(
            entity.getId(),
            entity.getUserId(),
            entity.getTotalAmount(),
            entity.getStatus(),
            items,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    private static OrderItemJpaEntity toItemEntity(OrderItem item, OrderJpaEntity orderEntity) {
        return new OrderItemJpaEntity(
            item.getId(),
            orderEntity,
            item.getProductId(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getSubtotal()
        );
    }

    private static OrderItem toItemDomain(OrderItemJpaEntity entity) {
        return OrderItem.reconstitute(
            entity.getId(),
            entity.getProductId(),
            entity.getProductName(),
            entity.getQuantity(),
            entity.getUnitPrice(),
            entity.getSubtotal()
        );
    }
}
