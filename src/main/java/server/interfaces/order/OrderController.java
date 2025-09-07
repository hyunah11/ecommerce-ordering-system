package server.interfaces.order;

import org.springframework.web.bind.annotation.*;
import server.application.order.PlaceOrderService;
import server.domain.order.Order;
import server.domain.order.OrderItem;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/orders/checkout")
public class OrderController {
    
    private final PlaceOrderService placeOrderService;
    
    public OrderController(PlaceOrderService placeOrderService) {
        this.placeOrderService = placeOrderService;
    }
    
    @PostMapping
    public OrderResponse placeOrder(@RequestBody OrderRequest request) {
        List<OrderItem> orderItems = request.getOrderItems().stream()
            .map(item -> OrderItem.create(item.getProductId(), item.getQty(), item.getUnitPrice()))
            .collect(Collectors.toList());
            
        Order order = placeOrderService.place(request.getUserId(), orderItems);
        return OrderResponse.from(order);
    }
}
