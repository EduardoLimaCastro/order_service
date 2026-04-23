package com.eduardocastro.order_service.infrastructure.web.controller;

import com.eduardocastro.order_service.application.usecase.AddCartItemUseCase;
import com.eduardocastro.order_service.application.usecase.CheckoutCartUseCase;
import com.eduardocastro.order_service.application.usecase.ClearCartUseCase;
import com.eduardocastro.order_service.application.usecase.GetCartByUserUseCase;
import com.eduardocastro.order_service.application.usecase.RemoveCartItemUseCase;
import com.eduardocastro.order_service.application.usecase.UpdateCartItemQuantityUseCase;
import com.eduardocastro.order_service.domain.exception.CartNotFoundException;
import com.eduardocastro.order_service.infrastructure.web.dto.request.AddCartItemRequest;
import com.eduardocastro.order_service.infrastructure.web.dto.request.UpdateCartItemQuantityRequest;
import com.eduardocastro.order_service.infrastructure.web.dto.response.CartResponse;
import com.eduardocastro.order_service.infrastructure.web.dto.response.OrderResponse;
import com.eduardocastro.order_service.infrastructure.web.mapper.CartWebMapper;
import com.eduardocastro.order_service.infrastructure.web.mapper.OrderWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/carts")
@RequiredArgsConstructor
public class CartController {

    private final GetCartByUserUseCase getCartByUserUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final CheckoutCartUseCase checkoutCartUseCase;

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getByUser(@PathVariable String userId) {
        return getCartByUserUseCase.execute(userId)
            .map(cart -> ResponseEntity.ok(CartWebMapper.toResponse(cart)))
            .orElseThrow(() -> new CartNotFoundException(userId));
    }

    @PostMapping("/user/{userId}/items")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable String userId,
            @Valid @RequestBody AddCartItemRequest request) {
        var cart = addCartItemUseCase.execute(userId, CartWebMapper.toItemInput(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CartWebMapper.toResponse(cart));
    }

    @PatchMapping("/user/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable String userId,
            @PathVariable UUID itemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        var cart = updateCartItemQuantityUseCase.execute(userId, itemId, request.quantity());
        return ResponseEntity.ok(CartWebMapper.toResponse(cart));
    }

    @DeleteMapping("/user/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable String userId,
            @PathVariable UUID itemId) {
        var cart = removeCartItemUseCase.execute(userId, itemId);
        return ResponseEntity.ok(CartWebMapper.toResponse(cart));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clear(@PathVariable String userId) {
        clearCartUseCase.execute(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/user/{userId}/checkout")
    public ResponseEntity<OrderResponse> checkout(@PathVariable String userId) {
        var order = checkoutCartUseCase.execute(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderWebMapper.toResponse(order));
    }
}
