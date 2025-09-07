package server.domain.order;

import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private int qty;
    private long unitPrice;
    private long subtotal;

    private OrderItem(Long productId, int qty, long unitPrice) {
        this.productId = productId;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.subtotal = qty * unitPrice;
    }

    public static OrderItem create(Long productId, int qty, long unitPrice) {
        return new OrderItem(productId, qty, unitPrice);
    }

    void assignOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void assignId(Long id) {
        this.id = id;
    }
}