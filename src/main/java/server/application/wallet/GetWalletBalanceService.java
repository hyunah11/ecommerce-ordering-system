package server.application.wallet;

import server.domain.wallet.Wallet;
import server.domain.wallet.WalletRepository;

/**
 * 순수 유스케이스 클래스
 * - 잔액 조회는 락이 불필요하므로 일반 조회 사용
 */
public class GetWalletBalanceService {
    private final WalletRepository walletRepository;
    
    public GetWalletBalanceService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /** 지갑 잔액 조회 유스케이스 */
    public Wallet getBalance(Long userId) {
        return walletRepository.findByUserId(userId)
            .orElse(Wallet.create(userId));  // 지갑이 없으면 0원으로 새로 생성
    }
}