package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.usecase.ClearCartUseCase;
import com.eduardocastro.order_service.domain.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClearCartInteractor implements ClearCartUseCase {

    private final CartRepository cartRepository;

    @Override
    @Transactional
    public void execute(String userId) {
        cartRepository.deleteByUserId(userId);
    }
}
