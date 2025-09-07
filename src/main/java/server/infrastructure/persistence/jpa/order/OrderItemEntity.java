package server.infrastructure.persistence.jpa.order;

import lombok.Data;
import jakarta.persistence.*;

@Entity @Table(name="order_items")
@Data
class OrderItemEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "order_id")
    Long orderId;
    
    @Column(name = "product_id")
    Long productId;
    
    int qty;
    
    @Column(name = "unit_price")
    Long unitPrice;
    
    Long subtotal;
}