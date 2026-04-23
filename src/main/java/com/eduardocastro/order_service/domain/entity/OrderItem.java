package com.eduardocastro.order_service.domain.entity;

import com.eduardocastro.order_service.domain.exception.InvalidOrderDataException;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {

    //==============
    // Attributes
    //==============

    private final UUID id;
    private final String productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal subtotal;

    //==============
    // Constructor
    //==============

    private OrderItem(UUID id, String productId, String productName, int quantity,
                      BigDecimal unitPrice, BigDecimal subtotal) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    //==============
    // Factories
    //==============

    public static OrderItem create(String productId, String productName, int quantity, BigDecimal unitPrice) {
        validate(productId, productName, quantity, unitPrice);
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new OrderItem(UUID.randomUUID(), productId, productName, quantity, unitPrice, subtotal);
    }

    public static OrderItem reconstitute(UUID id, String productId, String productName,
                                         int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        return new OrderItem(id, productId, productName, quantity, unitPrice, subtotal);
    }

    //==============
    // Getters
    //==============

    public UUID getId() { return id; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getSubtotal() { return subtotal; }

    //==============
    // Private helpers
    //==============

    private static void validate(String productId, String productName, int quantity, BigDecimal unitPrice) {
        if (productId == null || productId.isBlank())
            throw new InvalidOrderDataException("Product ID cannot be null or blank");
        if (productName == null || productName.isBlank())
            throw new InvalidOrderDataException("Product name cannot be null or blank");
        if (quantity <= 0)
            throw new InvalidOrderDataException("Quantity must be greater than zero");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidOrderDataException("Unit price must be greater than zero");
    }
}
