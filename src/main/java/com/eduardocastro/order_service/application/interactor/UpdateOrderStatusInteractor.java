package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.usecase.UpdateOrderStatusUseCase;
import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.enums.OrderStatus;
import com.eduardocastro.order_service.domain.exception.InvalidOrderDataException;
import com.eduardocastro.order_service.domain.exception.OrderNotFoundException;
import com.eduardocastro.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateOrderStatusInteractor implements UpdateOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Order execute(UUID id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

        switch (newStatus) {
            case CONFIRMED -> order.confirm();
            case SHIPPED   -> order.ship();
            case DELIVERED -> order.deliver();
            case CANCELLED -> order.cancel();
            default -> throw new InvalidOrderDataException("Cannot manually set status to: " + newStatus);
        }

        Order saved = orderRepository.save(order);
        order.pullDomainEvents().forEach(eventPublisher::publishEvent);
        return saved;
    }
}
