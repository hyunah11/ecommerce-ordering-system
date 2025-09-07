package server.domain.wallet;

import java.util.Optional;

public interface WalletRepository {
    Optional<Wallet> findByUserId(Long userId);
    Optional<Wallet> findByUserIdWithLock(Long userId);  // 비관적 락
    Wallet save(Wallet wallet);
}
