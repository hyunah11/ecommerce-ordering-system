package server.infrastructure.persistence.jpa.order;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import server.domain.order.Order;
import server.domain.order.OrderItem;
import server.domain.order.OrderRepository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
class OrderJpaRepository implements OrderRepository {
    private final SpringOrderJpa orderJpa;
    private final SpringOrderItemJpa orderItemJpa;
    
    public OrderJpaRepository(SpringOrderJpa orderJpa, SpringOrderItemJpa orderItemJpa) {
        this.orderJpa = orderJpa;
        this.orderItemJpa = orderItemJpa;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        // 1. Order 엔티티 저장
        OrderEntity orderEntity = toOrderEntity(order);
        OrderEntity savedOrderEntity = orderJpa.save(orderEntity);
        order.assignId(savedOrderEntity.id);
        
        // 2. OrderItem 엔티티들 저장
        List<OrderItemEntity> orderItemEntities = order.getOrderItems().stream()
            .map(this::toOrderItemEntity)
            .collect(Collectors.toList());
        
        List<OrderItemEntity> savedOrderItemEntities = orderItemJpa.saveAll(orderItemEntities);
        
        // 3. OrderItem ID 할당
        for (int i = 0; i < savedOrderItemEntities.size(); i++) {
            order.getOrderItems().get(i).assignId(savedOrderItemEntities.get(i).getId());
        }
        
        return order;
    }

    /** Domain → JPA DTO 매핑 (Infra 전용) */
    private OrderEntity toOrderEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.id = order.getId();
        entity.userId = order.getUserId();
        entity.status = order.getStatus();
        entity.subtotal = order.getSubtotal();
        entity.discount = order.getDiscount();
        entity.totalAmount = order.getTotalAmount();
        entity.createdAt = order.getCreatedAt();
        entity.updatedAt = order.getUpdatedAt();
        return entity;
    }
    
    private OrderItemEntity toOrderItemEntity(OrderItem orderItem) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.id = orderItem.getId();
        entity.orderId = orderItem.getOrderId();
        entity.productId = orderItem.getProductId();
        entity.qty = orderItem.getQty();
        entity.unitPrice = orderItem.getUnitPrice();
        entity.subtotal = orderItem.getSubtotal();
        return entity;
    }
}