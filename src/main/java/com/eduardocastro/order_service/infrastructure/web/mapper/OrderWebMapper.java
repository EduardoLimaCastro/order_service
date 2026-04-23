package com.eduardocastro.order_service.infrastructure.web.mapper;

import com.eduardocastro.order_service.application.dto.OrderItemInput;
import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.entity.OrderItem;
import com.eduardocastro.order_service.infrastructure.web.dto.request.CreateOrderItemRequest;
import com.eduardocastro.order_service.infrastructure.web.dto.response.OrderItemResponse;
import com.eduardocastro.order_service.infrastructure.web.dto.response.OrderResponse;

import java.time.format.DateTimeFormatter;

public class OrderWebMapper {

    private OrderWebMapper() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount(),
            order.getStatus().name(),
            order.getItems().stream().map(OrderWebMapper::toItemResponse).toList(),
            order.getCreatedAt().format(FORMATTER),
            order.getUpdatedAt().format(FORMATTER)
        );
    }

    public static OrderItemInput toItemInput(CreateOrderItemRequest request) {
        return new OrderItemInput(
            request.productId(),
            request.productName(),
            request.quantity(),
            request.unitPrice()
        );
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
            item.getId(),
            item.getProductId(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getSubtotal()
        );
    }
}
