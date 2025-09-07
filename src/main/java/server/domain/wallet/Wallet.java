package server.domain.wallet;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Wallet {
    private Long userId;
    private Long balance;
    private LocalDateTime updatedAt;

    private Wallet(Long userId, Long balance) {
        this.userId = userId;
        this.balance = balance;
        this.updatedAt = LocalDateTime.now();
    }

    public static Wallet create(Long userId) {
        return new Wallet(userId, 0L);
    }

    public void charge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.balance += amount;
        this.updatedAt = LocalDateTime.now();
    }
}