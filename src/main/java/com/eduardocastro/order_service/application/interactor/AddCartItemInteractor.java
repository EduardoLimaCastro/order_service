package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.dto.OrderItemInput;
import com.eduardocastro.order_service.application.usecase.AddCartItemUseCase;
import com.eduardocastro.order_service.domain.entity.Cart;
import com.eduardocastro.order_service.domain.entity.CartItem;
import com.eduardocastro.order_service.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddCartItemInteractor implements AddCartItemUseCase {

    private final CartRepository cartRepository;

    @Override
    public Cart execute(String userId, OrderItemInput item) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> Cart.create(userId));

        CartItem cartItem = CartItem.create(
            item.productId(),
            item.productName(),
            item.quantity(),
            item.unitPrice()
        );
        cart.addItem(cartItem);

        return cartRepository.save(cart);
    }
}
