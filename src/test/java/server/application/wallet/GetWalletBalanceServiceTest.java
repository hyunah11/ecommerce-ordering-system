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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("지갑 잔액 조회 서비스 테스트")
class GetWalletBalanceServiceTest {

    @Mock
    private WalletRepository walletRepository;

    private GetWalletBalanceService getWalletBalanceService;

    @BeforeEach
    void setUp() {
        getWalletBalanceService = new GetWalletBalanceService(walletRepository);
    }

    @Test
    @DisplayName("기존 지갑 잔액 조회 - 성공")
    void getBalance_ExistingWallet_ReturnsWallet() {
        Long userId = 1L;
        long expectedBalance = 15000L;
        
        Wallet existingWallet = Wallet.create(userId);
        existingWallet.charge(expectedBalance);

        when(walletRepository.findByUserId(userId))
            .thenReturn(Optional.of(existingWallet));

        Wallet result = getWalletBalanceService.getBalance(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(expectedBalance);
        
        verify(walletRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("존재하지 않는 지갑 조회 - 새 지갑 생성하여 반환")
    void getBalance_NonExistentWallet_ReturnsNewWallet() {
        Long userId = 1L;

        when(walletRepository.findByUserId(userId))
            .thenReturn(Optional.empty());

        Wallet result = getWalletBalanceService.getBalance(userId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(0L);
        
        verify(walletRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("여러 번 조회해도 동일한 결과")
    void getBalance_MultipleQueries_ConsistentResults() {
        Long userId = 1L;
        long expectedBalance = 25000L;
        
        Wallet existingWallet = Wallet.create(userId);
        existingWallet.charge(expectedBalance);

        when(walletRepository.findByUserId(userId))
            .thenReturn(Optional.of(existingWallet));

        Wallet result1 = getWalletBalanceService.getBalance(userId);
        Wallet result2 = getWalletBalanceService.getBalance(userId);

        assertThat(result1.getUserId()).isEqualTo(userId);
        assertThat(result1.getBalance()).isEqualTo(expectedBalance);
        assertThat(result2.getUserId()).isEqualTo(userId);
        assertThat(result2.getBalance()).isEqualTo(expectedBalance);
    }
}