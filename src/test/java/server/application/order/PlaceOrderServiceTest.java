package server.application.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.domain.order.*;
import server.domain.wallet.Wallet;
import server.domain.wallet.WalletRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 생성 서비스 테스트")
class PlaceOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private WalletRepository walletRepository;

    private PlaceOrderService placeOrderService;

    @BeforeEach
    void setUp() {
        placeOrderService = new PlaceOrderService(orderRepository, walletRepository);
    }

    @Test
    @DisplayName("충분한 잔액으로 주문 생성 - 성공")
    void place_SufficientBalance_Success() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = Arrays.asList(
            OrderItem.create(1L, 2, 10000L),
            OrderItem.create(2L, 1, 5000L)
        );
        long totalAmount = 25000L; // 2*10000 + 1*5000
        long walletBalance = 30000L; // 충분한 잔액

        Wallet wallet = Wallet.create(userId);
        wallet.charge(walletBalance);

        when(walletRepository.findByUserId(userId))
            .thenReturn(Optional.of(wallet));
        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = placeOrderService.place(userId, orderItems);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTotalAmount()).isEqualTo(totalAmount);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);

        verify(walletRepository).findByUserId(userId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("지갑이 없는 사용자 주문 시도 - 잔액 부족으로 실패")
    void place_NoWallet_InsufficientBalanceException() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = Arrays.asList(
            OrderItem.create(1L, 2, 10000L)
        );
        long totalAmount = 20000L;

        when(walletRepository.findByUserId(userId))
            .thenReturn(Optional.empty()); // 지갑 없음 -> 0원 잔액

        // when & then
        assertThatThrownBy(() -> placeOrderService.place(userId, orderItems))
            .isInstanceOf(InsufficientBalanceException.class)
            .hasMessage("잔액이 부족합니다. 필요 금액: 20000원, 현재 잔액: 0원");

        verify(walletRepository).findByUserId(userId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("잔액 부족으로 주문 실패")
    void place_InsufficientBalance_Exception() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = Arrays.asList(
            OrderItem.create(1L, 3, 10000L)
        );
        long totalAmount = 30000L;
        long walletBalance = 25000L; // 부족한 잔액

        Wallet wallet = Wallet.create(userId);
        wallet.charge(walletBalance);

        when(walletRepository.findByUserId(userId))
            .thenReturn(Optional.of(wallet));

        // when & then
        assertThatThrownBy(() -> placeOrderService.place(userId, orderItems))
            .isInstanceOf(InsufficientBalanceException.class)
            .hasMessage("잔액이 부족합니다. 필요 금액: 30000원, 현재 잔액: 25000원");

        verify(walletRepository).findByUserId(userId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("정확히 같은 잔액으로 주문 생성 - 성공")
    void place_ExactBalance_Success() {
        // given
        Long userId = 1L;
        List<OrderItem> orderItems = Arrays.asList(
            OrderItem.create(1L, 1, 15000L)
        );
        long totalAmount = 15000L;
        long walletBalance = 15000L; // 정확히 같은 잔액

        Wallet wallet = Wallet.create(userId);
        wallet.charge(walletBalance);

        when(walletRepository.findByUserId(userId))
            .thenReturn(Optional.of(wallet));
        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = placeOrderService.place(userId, orderItems);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTotalAmount()).isEqualTo(totalAmount);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);

        verify(walletRepository).findByUserId(userId);
        verify(orderRepository).save(any(Order.class));
    }
}