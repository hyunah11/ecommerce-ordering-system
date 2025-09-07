package server.application.wallet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.domain.wallet.Wallet;
import server.domain.wallet.WalletRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("지갑 충전 서비스 테스트")
class ChargeWalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    private ChargeWalletService chargeWalletService;

    @BeforeEach
    void setUp() {
        chargeWalletService = new ChargeWalletService(walletRepository);
    }

    @Test
    @DisplayName("새로운 지갑에 충전 - 성공")
    void charge_NewWallet_Success() {
        Long userId = 1L;
        long chargeAmount = 10000L;
        
        when(walletRepository.findByUserIdWithLock(userId))
            .thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = chargeWalletService.charge(userId, chargeAmount);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(chargeAmount);
        
        verify(walletRepository).findByUserIdWithLock(userId);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    @DisplayName("기존 지갑에 충전 - 성공")
    void charge_ExistingWallet_Success() {
        Long userId = 1L;
        long existingBalance = 5000L;
        long chargeAmount = 3000L;
        long expectedBalance = existingBalance + chargeAmount;

        Wallet existingWallet = Wallet.create(userId);
        existingWallet.charge(existingBalance);

        when(walletRepository.findByUserIdWithLock(userId))
            .thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Wallet result = chargeWalletService.charge(userId, chargeAmount);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(expectedBalance);
        
        verify(walletRepository).findByUserIdWithLock(userId);
        verify(walletRepository).save(existingWallet);
    }

    @Test
    @DisplayName("0원 이하 충전 시도 - 실패")
    void charge_InvalidAmount_ThrowsException() {
        Long userId = 1L;
        long invalidAmount = 0L;

        when(walletRepository.findByUserIdWithLock(userId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> chargeWalletService.charge(userId, invalidAmount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");

        verify(walletRepository).findByUserIdWithLock(userId);
        verify(walletRepository, never()).save(any());
    }

    @Test
    @DisplayName("음수 충전 시도 - 실패")
    void charge_NegativeAmount_ThrowsException() {
        Long userId = 1L;
        long negativeAmount = -1000L;

        when(walletRepository.findByUserIdWithLock(userId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> chargeWalletService.charge(userId, negativeAmount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");

        verify(walletRepository).findByUserIdWithLock(userId);
        verify(walletRepository, never()).save(any());
    }
}