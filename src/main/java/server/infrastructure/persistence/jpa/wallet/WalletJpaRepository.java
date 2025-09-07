package server.infrastructure.persistence.jpa.wallet;

import org.springframework.stereotype.Repository;
import server.domain.wallet.Wallet;
import server.domain.wallet.WalletRepository;
import java.util.Optional;

@Repository
class WalletJpaRepository implements WalletRepository {
    private final SpringWalletJpa walletJpa;
    
    public WalletJpaRepository(SpringWalletJpa walletJpa) {
        this.walletJpa = walletJpa;
    }

    @Override
    public Optional<Wallet> findByUserId(Long userId) {
        return walletJpa.findByUserId(userId)
            .map(this::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserIdWithLock(Long userId) {
        return walletJpa.findByUserIdWithLock(userId)
            .map(this::toDomain);
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = toEntity(wallet);
        WalletEntity savedEntity = walletJpa.save(entity);
        return toDomain(savedEntity);
    }

    /** Domain → JPA DTO 매핑 */
    private WalletEntity toEntity(Wallet wallet) {
        WalletEntity entity = new WalletEntity();
        entity.setUserId(wallet.getUserId());
        entity.setBalance(wallet.getBalance());
        entity.setUpdatedAt(wallet.getUpdatedAt());
        return entity;
    }

    /** JPA DTO → Domain 매핑 */
    private Wallet toDomain(WalletEntity entity) {
        Wallet wallet = Wallet.create(entity.getUserId());
        wallet.setBalance(entity.getBalance());
        wallet.setUpdatedAt(entity.getUpdatedAt());
        return wallet;
    }
}
