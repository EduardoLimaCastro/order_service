package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.usecase.RemoveCartItemUseCase;
import com.eduardocastro.order_service.domain.entity.Cart;
import com.eduardocastro.order_service.domain.exception.CartNotFoundException;
import com.eduardocastro.order_service.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoveCartItemInteractor implements RemoveCartItemUseCase {

    private final CartRepository cartRepository;

    @Override
    public Cart execute(String userId, UUID itemId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException(userId));
        cart.removeItem(itemId);
        return cartRepository.save(cart);
    }
}
