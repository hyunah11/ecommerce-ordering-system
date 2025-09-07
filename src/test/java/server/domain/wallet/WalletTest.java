package server.domain.wallet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지갑 도메인 테스트")
class WalletTest {

    @Test
    @DisplayName("새 지갑 생성 - 초기 잔액 0원")
    void create_NewWallet_InitialBalanceZero() {
        Long userId = 1L;

        Wallet wallet = Wallet.create(userId);

        assertThat(wallet.getUserId()).isEqualTo(userId);
        assertThat(wallet.getBalance()).isEqualTo(0L);
        assertThat(wallet.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("지갑 충전 - 성공")
    void charge_ValidAmount_Success() {
        Long userId = 1L;
        long chargeAmount = 10000L;
        Wallet wallet = Wallet.create(userId);

        wallet.charge(chargeAmount);

        assertThat(wallet.getBalance()).isEqualTo(chargeAmount);
        assertThat(wallet.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("지갑 여러번 충전 - 잔액 누적")
    void charge_MultipleCharges_BalanceAccumulates() {
        Long userId = 1L;
        Wallet wallet = Wallet.create(userId);

        wallet.charge(5000L);
        wallet.charge(3000L);
        wallet.charge(2000L);

        assertThat(wallet.getBalance()).isEqualTo(10000L);
    }

    @Test
    @DisplayName("0원 충전 시도 - 예외 발생")
    void charge_ZeroAmount_ThrowsException() {
        Long userId = 1L;
        Wallet wallet = Wallet.create(userId);

        // when & then
        assertThatThrownBy(() -> wallet.charge(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");

        assertThat(wallet.getBalance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("음수 충전 시도 - 예외 발생")
    void charge_NegativeAmount_ThrowsException() {
        Long userId = 1L;
        Wallet wallet = Wallet.create(userId);

        assertThatThrownBy(() -> wallet.charge(-1000L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("충전 금액은 0보다 커야 합니다.");

        assertThat(wallet.getBalance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("충전 후 잔액 불변성 확인")
    void charge_BalanceImmutability_Success() {
        Long userId = 1L;

        Wallet wallet = Wallet.create(userId);
        wallet.charge(10000L);
        long initialBalance = wallet.getBalance();
        long currentBalance = wallet.getBalance();

        assertThat(currentBalance).isEqualTo(initialBalance);
    }
}