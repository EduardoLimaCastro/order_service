package com.eduardocastro.order_service.domain.entity;

import com.eduardocastro.order_service.domain.exception.InvalidCartDataException;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItem {

    //==============
    // Attributes
    //==============

    private final UUID id;
    private final String productId;
    private final String productName;
    private int quantity;
    private final BigDecimal unitPrice;
    private BigDecimal subtotal;

    //==============
    // Constructor
    //==============

    private CartItem(UUID id, String productId, String productName, int quantity,
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

    public static CartItem create(String productId, String productName, int quantity, BigDecimal unitPrice) {
        validate(productId, productName, quantity, unitPrice);
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new CartItem(UUID.randomUUID(), productId, productName, quantity, unitPrice, subtotal);
    }

    public static CartItem reconstitute(UUID id, String productId, String productName,
                                        int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        return new CartItem(id, productId, productName, quantity, unitPrice, subtotal);
    }

    //==============
    // Domain Methods
    //==============

    public void changeQuantity(int newQuantity) {
        if (newQuantity <= 0)
            throw new InvalidCartDataException("Quantity must be greater than zero");
        this.quantity = newQuantity;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(newQuantity));
    }

    public void addQuantity(int additional) {
        if (additional <= 0)
            throw new InvalidCartDataException("Additional quantity must be greater than zero");
        changeQuantity(this.quantity + additional);
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
            throw new InvalidCartDataException("Product ID cannot be null or blank");
        if (productName == null || productName.isBlank())
            throw new InvalidCartDataException("Product name cannot be null or blank");
        if (quantity <= 0)
            throw new InvalidCartDataException("Quantity must be greater than zero");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidCartDataException("Unit price must be greater than zero");
    }
}
