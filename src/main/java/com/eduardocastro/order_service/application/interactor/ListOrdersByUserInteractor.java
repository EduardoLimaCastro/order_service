package com.eduardocastro.order_service.application.interactor;

import com.eduardocastro.order_service.application.usecase.ListOrdersByUserUseCase;
import com.eduardocastro.order_service.domain.entity.Order;
import com.eduardocastro.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListOrdersByUserInteractor implements ListOrdersByUserUseCase {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> execute(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
