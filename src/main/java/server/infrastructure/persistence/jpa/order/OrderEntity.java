package server.infrastructure.persistence.jpa.order;

import lombok.Data;
import jakarta.persistence.*;
import server.domain.order.OrderStatus;
import java.time.LocalDateTime;

@Entity @Table(name="orders")
@Data
class OrderEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;
    
    @Column(name = "user_id")
    Long userId;
    
    @Enumerated(EnumType.STRING)
    OrderStatus status;
    
    Long subtotal;
    Long discount;
    
    @Column(name = "total_amount")
    Long totalAmount;
    
    @Column(name = "created_at")
    LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}