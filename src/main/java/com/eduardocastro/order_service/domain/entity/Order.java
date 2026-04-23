package com.eduardocastro.order_service.domain.entity;

import com.eduardocastro.order_service.domain.enums.OrderStatus;
import com.eduardocastro.order_service.domain.event.DomainEvent;
import com.eduardocastro.order_service.domain.exception.InvalidOrderDataException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {

    //==============
    // Attributes
    //==============

    private final UUID id;
    private final String userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    //==============
    // Constructor
    //==============

    private Order(UUID id, String userId, BigDecimal totalAmount, OrderStatus status,
                  List<OrderItem> items, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //==============
    // Factories
    //==============

    public static Order create(String userId, List<OrderItem> items) {
        validate(userId, items);
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(UUID.randomUUID(), userId, BigDecimal.ZERO, OrderStatus.PENDING, items, now, now);
        order.recalculateTotal();
        return order;
    }

    public static Order reconstitute(UUID id, String userId, BigDecimal totalAmount, OrderStatus status,
                                     List<OrderItem> items, LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateReconstitute(id, userId, createdAt, updatedAt);
        return new Order(id, userId, totalAmount, status, items, createdAt, updatedAt);
    }

    //==============
    // Domain Methods
    //==============

    public void addItem(OrderItem item) {
        if (item == null) throw new InvalidOrderDataException("Order item cannot be null");
        items.add(item);
        recalculateTotal();
        touch();
    }

    public void confirm() {
        validateTransition(OrderStatus.CONFIRMED);
        status = OrderStatus.CONFIRMED;
        touch();
    }

    public void ship() {
        validateTransition(OrderStatus.SHIPPED);
        status = OrderStatus.SHIPPED;
        touch();
    }

    public void deliver() {
        validateTransition(OrderStatus.DELIVERED);
        status = OrderStatus.DELIVERED;
        touch();
    }

    public void cancel() {
        validateTransition(OrderStatus.CANCELLED);
        status = OrderStatus.CANCELLED;
        touch();
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
        return id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    //==============
    // Getters
    //==============

    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    //==============
    // Private helpers
    //==============

    private static void validate(String userId, List<OrderItem> items) {
        if (userId == null || userId.isBlank())
            throw new InvalidOrderDataException("User ID cannot be null or blank");
        if (items == null || items.isEmpty())
            throw new InvalidOrderDataException("Order must have at least one item");
    }

    private static void validateReconstitute(UUID id, String userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null)
            throw new InvalidOrderDataException("Order ID cannot be null for reconstitution");
        if (userId == null || userId.isBlank())
            throw new InvalidOrderDataException("User ID cannot be null or blank");
        if (createdAt == null)
            throw new InvalidOrderDataException("CreatedAt cannot be null for reconstitution");
        if (updatedAt == null || updatedAt.isBefore(createdAt))
            throw new InvalidOrderDataException("UpdatedAt cannot be before createdAt");
    }

    private void validateTransition(OrderStatus target) {
        boolean valid = switch (target) {
            case CONFIRMED -> status == OrderStatus.PENDING;
            case SHIPPED   -> status == OrderStatus.CONFIRMED;
            case DELIVERED -> status == OrderStatus.SHIPPED;
            case CANCELLED -> status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED;
            default -> false;
        };
        if (!valid) {
            throw new InvalidOrderDataException(
                "Cannot transition order from %s to %s".formatted(status, target)
            );
        }
    }

    private void recalculateTotal() {
        totalAmount = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void touch() {
        updatedAt = LocalDateTime.now();
    }
}
