package com.eduardocastro.order_service.infrastructure.web.mapper;

import com.eduardocastro.order_service.application.dto.OrderItemInput;
import com.eduardocastro.order_service.domain.entity.Cart;
import com.eduardocastro.order_service.domain.entity.CartItem;
import com.eduardocastro.order_service.infrastructure.web.dto.request.AddCartItemRequest;
import com.eduardocastro.order_service.infrastructure.web.dto.response.CartItemResponse;
import com.eduardocastro.order_service.infrastructure.web.dto.response.CartResponse;

import java.time.format.DateTimeFormatter;

public class CartWebMapper {

    private CartWebMapper() {}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static CartResponse toResponse(Cart cart) {
        return new CartResponse(
            cart.getId(),
            cart.getUserId(),
            cart.getTotalAmount(),
            cart.getItems().stream().map(CartWebMapper::toItemResponse).toList(),
            cart.getCreatedAt().format(FORMATTER),
            cart.getUpdatedAt().format(FORMATTER)
        );
    }

    public static OrderItemInput toItemInput(AddCartItemRequest request) {
        return new OrderItemInput(
            request.productId(),
            request.productName(),
            request.quantity(),
            request.unitPrice()
        );
    }

    private static CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
            item.getId(),
            item.getProductId(),
            item.getProductName(),
            item.getQuantity(),
            item.getUnitPrice(),
            item.getSubtotal()
        );
    }
}
