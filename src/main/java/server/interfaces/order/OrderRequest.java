package server.interfaces.order;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> orderItems;
    
    @Data
    public static class OrderItemRequest {
        private Long productId;
        private int qty;
        private long unitPrice;
    }
}