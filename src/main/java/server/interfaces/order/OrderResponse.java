package server.interfaces.order;

import lombok.Data;
import server.domain.order.Order;
import server.domain.order.OrderItem;
import server.domain.order.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private long subtotal;
    private long discount;
    private long totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> orderItems;
    
    @Data
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private int qty;
        private long unitPrice;
        private long subtotal;
        
        public static OrderItemResponse from(OrderItem orderItem) {
            OrderItemResponse response = new OrderItemResponse();
            response.id = orderItem.getId();
            response.productId = orderItem.getProductId();
            response.qty = orderItem.getQty();
            response.unitPrice = orderItem.getUnitPrice();
            response.subtotal = orderItem.getSubtotal();
            return response;
        }
    }
    
    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.id = order.getId();
        response.userId = order.getUserId();
        response.status = order.getStatus();
        response.subtotal = order.getSubtotal();
        response.discount = order.getDiscount();
        response.totalAmount = order.getTotalAmount();
        response.createdAt = order.getCreatedAt();
        response.updatedAt = order.getUpdatedAt();
        response.orderItems = order.getOrderItems().stream()
            .map(OrderItemResponse::from)
            .collect(Collectors.toList());
        return response;
    }
}