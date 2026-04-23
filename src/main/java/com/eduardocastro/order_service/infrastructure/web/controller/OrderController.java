package com.eduardocastro.order_service.infrastructure.web.controller;

import com.eduardocastro.order_service.application.usecase.CreateOrderUseCase;
import com.eduardocastro.order_service.application.usecase.GetOrderByIdUseCase;
import com.eduardocastro.order_service.application.usecase.ListOrdersByUserUseCase;
import com.eduardocastro.order_service.application.usecase.UpdateOrderStatusUseCase;
import com.eduardocastro.order_service.domain.exception.OrderNotFoundException;
import com.eduardocastro.order_service.infrastructure.web.dto.request.CreateOrderRequest;
import com.eduardocastro.order_service.infrastructure.web.dto.request.UpdateOrderStatusRequest;
import com.eduardocastro.order_service.infrastructure.web.dto.response.OrderResponse;
import com.eduardocastro.order_service.infrastructure.web.mapper.OrderWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final ListOrdersByUserUseCase listOrdersByUserUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        var items = request.items().stream()
            .map(OrderWebMapper::toItemInput)
            .toList();
        var order = createOrderUseCase.execute(request.userId(), items);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderWebMapper.toResponse(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
        return getOrderByIdUseCase.execute(id)
            .map(order -> ResponseEntity.ok(OrderWebMapper.toResponse(order)))
            .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getByUser(@PathVariable String userId) {
        var orders = listOrdersByUserUseCase.execute(userId).stream()
            .map(OrderWebMapper::toResponse)
            .toList();
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        var order = updateOrderStatusUseCase.execute(id, request.status());
        return ResponseEntity.ok(OrderWebMapper.toResponse(order));
    }
}
