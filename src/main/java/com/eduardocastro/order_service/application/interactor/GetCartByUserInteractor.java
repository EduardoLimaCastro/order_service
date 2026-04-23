package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.usecase.GetCartByUserUseCase;
import com.eduardocastro.order_service.domain.entity.Cart;
import com.eduardocastro.order_service.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetCartByUserInteractor implements GetCartByUserUseCase {

    private final CartRepository cartRepository;

    @Override
    public Optional<Cart> execute(String userId) {
        return cartRepository.findByUserId(userId);
    }
}
