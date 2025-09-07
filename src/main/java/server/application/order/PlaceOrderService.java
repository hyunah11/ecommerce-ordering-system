package server.application.order;

import server.domain.order.Order;
import server.domain.order.OrderItem;
import server.domain.order.OrderRepository;
import server.domain.order.InsufficientBalanceException;
import server.domain.wallet.Wallet;
import server.domain.wallet.WalletRepository;
import java.util.List;

/**
 * 순수 유스케이스 클래스
 * - @Service, 스프링 의존성 제거 (DI는 구성 클래스에서)
 * - 트랜잭션 경계만 남기고, 프레임워크·DB 세부 지식 없음
 */
public class PlaceOrderService {
    // 도메인 포트(인터페이스)에만 의존
    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;

    public PlaceOrderService(OrderRepository orderRepository, WalletRepository walletRepository) {
        this.orderRepository = orderRepository;
        this.walletRepository = walletRepository;
    }

    /** 주문 생성 유스케이스 - 지갑 잔액 확인 후 주문 처리 */
    @org.springframework.transaction.annotation.Transactional
    public Order place(Long userId, List<OrderItem> orderItems) {
        Order order = Order.create(userId, orderItems);
        
        // 지갑 잔액 확인
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElse(Wallet.create(userId)); // 지갑이 없으면 0원으로 새로 생성
            
        // 잔액 부족 시 예외 발생 (todo: 예외처리 추가 구현)
        if (wallet.getBalance() < order.getTotalAmount()) {
            throw new InsufficientBalanceException(order.getTotalAmount(), wallet.getBalance());
        }
        
        return orderRepository.save(order);          // 추상 포트 호출
    }
}