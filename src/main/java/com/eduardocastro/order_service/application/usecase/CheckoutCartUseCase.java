package com.eduardocastro.order_service.application.usecase;

import com.eduardocastro.order_service.domain.entity.Order;

public interface CheckoutCartUseCase {
    Order execute(String userId);
}
