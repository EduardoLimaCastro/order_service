package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.usecase.GetOrderByIdUseCase;
import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrderByIdInteractor implements GetOrderByIdUseCase {

    private final OrderRepository orderRepository;

    @Override
    public Optional<Order> execute(UUID id) {
        return orderRepository.findById(id);
    }
}
