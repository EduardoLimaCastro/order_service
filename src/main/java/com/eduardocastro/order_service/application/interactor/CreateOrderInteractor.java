package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.dto.OrderItemInput;
import com.eduardocastro.order_service.application.usecase.CreateOrderUseCase;
import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.entity.OrderItem;
import com.eduardocastro.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderInteractor implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Order execute(String userId, List<OrderItemInput> items) {
        List<OrderItem> orderItems = items.stream()
            .map(i -> OrderItem.create(i.productId(), i.productName(), i.quantity(), i.unitPrice()))
            .toList();

        Order order = Order.create(userId, orderItems);
        Order saved = orderRepository.save(order);
        order.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
