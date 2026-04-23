package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.usecase.UpdateCartItemQuantityUseCase;
import com.eduardocastro.order_service.domain.entity.Cart;
import com.eduardocastro.order_service.domain.exception.CartNotFoundException;
import com.eduardocastro.order_service.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCartItemQuantityInteractor implements UpdateCartItemQuantityUseCase {

    private final CartRepository cartRepository;

    @Override
    public Cart execute(String userId, UUID itemId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException(userId));
        cart.updateItemQuantity(itemId, quantity);
        return cartRepository.save(cart);
    }
}
