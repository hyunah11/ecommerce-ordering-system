package server.domain.order;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
public class Order {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private long subtotal;
    private long discount;
    private long totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(Long userId, List<OrderItem> orderItems) {
        this.userId = userId;
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.orderItems = new ArrayList<>(orderItems);
        calculateAmounts();
    }
    
    public static Order create(Long userId, List<OrderItem> orderItems) {
        return new Order(userId, orderItems);
    }
    
    private void calculateAmounts() {
        this.subtotal = orderItems.stream()
            .mapToLong(OrderItem::getSubtotal)
            .sum();
        this.discount = 0L; // 초기값, 쿠폰 적용 시 변경
        this.totalAmount = this.subtotal - this.discount;
    }
    
    public void applyDiscount(long discount) {
        this.discount = discount;
        this.totalAmount = this.subtotal - this.discount;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsPaid() {
        this.status = OrderStatus.PAID;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = OrderStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void assignId(Long id) {
        this.id = id;
        // OrderItem에도 orderId 할당
        orderItems.forEach(item -> item.assignOrderId(id));
    }
}