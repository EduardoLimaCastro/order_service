package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.dto.OrderItemInput;
import com.eduardocastro.order_service.application.usecase.CheckoutCartUseCase;
import com.eduardocastro.order_service.application.usecase.CreateOrderUseCase;
import com.eduardocastro.order_service.domain.entity.Cart;
import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.event.CartCheckedOutEvent;
import com.eduardocastro.order_service.domain.exception.CartNotFoundException;
import com.eduardocastro.order_service.domain.exception.InvalidCartDataException;
import com.eduardocastro.order_service.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutCartInteractor implements CheckoutCartUseCase {

    private final CartRepository cartRepository;
    private final CreateOrderUseCase createOrderUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Order execute(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException(userId));

        if (cart.isEmpty())
            throw new InvalidCartDataException("Cannot checkout an empty cart");

        List<OrderItemInput> items = cart.getItems().stream()
            .map(i -> new OrderItemInput(i.getProductId(), i.getProductName(), i.getQuantity(), i.getUnitPrice()))
            .toList();

        Order order = createOrderUseCase.execute(userId, items);
        cartRepository.deleteByUserId(userId);

        eventPublisher.publishEvent(new CartCheckedOutEvent(
            cart.getId(), userId, order.getId(), LocalDateTime.now()
        ));

        return order;
    }
}
