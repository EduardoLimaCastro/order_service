package com.eduardocastro.order_service.domain.entity;

import com.eduardocastro.order_service.domain.event.DomainEvent;
import com.eduardocastro.order_service.domain.exception.InvalidCartDataException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Cart {

    //==============
    // Attributes
    //==============

    private final UUID id;
    private final String userId;
    private BigDecimal totalAmount;
    private final List<CartItem> items;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    //==============
    // Constructor
    //==============

    private Cart(UUID id, String userId, BigDecimal totalAmount, List<CartItem> items,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //==============
    // Factories
    //==============

    public static Cart create(String userId) {
        if (userId == null || userId.isBlank())
            throw new InvalidCartDataException("User ID cannot be null or blank");
        LocalDateTime now = LocalDateTime.now();
        return new Cart(UUID.randomUUID(), userId, BigDecimal.ZERO, new ArrayList<>(), now, now);
    }

    public static Cart reconstitute(UUID id, String userId, BigDecimal totalAmount,
                                    List<CartItem> items, LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateReconstitute(id, userId, createdAt, updatedAt);
        return new Cart(id, userId, totalAmount, items, createdAt, updatedAt);
    }

    //==============
    // Domain Methods
    //==============

    public void addItem(CartItem newItem) {
        if (newItem == null) throw new InvalidCartDataException("Cart item cannot be null");

        Optional<CartItem> existing = findByProductId(newItem.getProductId());
        if (existing.isPresent()) {
            existing.get().addQuantity(newItem.getQuantity());
        } else {
            items.add(newItem);
        }
        recalculateTotal();
        touch();
    }

    public void updateItemQuantity(UUID itemId, int newQuantity) {
        CartItem item = findById(itemId)
            .orElseThrow(() -> new InvalidCartDataException("Cart item not found: " + itemId));
        item.changeQuantity(newQuantity);
        recalculateTotal();
        touch();
    }

    public void removeItem(UUID itemId) {
        boolean removed = items.removeIf(i -> i.getId().equals(itemId));
        if (!removed)
            throw new InvalidCartDataException("Cart item not found: " + itemId);
        recalculateTotal();
        touch();
    }

    public void clear() {
        items.clear();
        recalculateTotal();
        touch();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = Collections.unmodifiableList(new ArrayList<>(domainEvents));
        domainEvents.clear();
        return events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id.equals(((Cart) o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    //==============
    // Getters
    //==============

    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<CartItem> getItems() { return Collections.unmodifiableList(items); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    //==============
    // Private helpers
    //==============

    private Optional<CartItem> findByProductId(String productId) {
        return items.stream().filter(i -> i.getProductId().equals(productId)).findFirst();
    }

    private Optional<CartItem> findById(UUID itemId) {
        return items.stream().filter(i -> i.getId().equals(itemId)).findFirst();
    }

    private static void validateReconstitute(UUID id, String userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null)
            throw new InvalidCartDataException("Cart ID cannot be null for reconstitution");
        if (userId == null || userId.isBlank())
            throw new InvalidCartDataException("User ID cannot be null or blank");
        if (createdAt == null)
            throw new InvalidCartDataException("CreatedAt cannot be null for reconstitution");
        if (updatedAt == null || updatedAt.isBefore(createdAt))
            throw new InvalidCartDataException("UpdatedAt cannot be before createdAt");
    }

    private void recalculateTotal() {
        totalAmount = items.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void touch() {
        updatedAt = LocalDateTime.now();
    }
}
