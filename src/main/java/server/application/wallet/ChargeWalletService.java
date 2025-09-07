package server.application.wallet;

import server.domain.wallet.Wallet;
import server.domain.wallet.WalletRepository;

/**
 * 순수 유스케이스 클래스
 * - @Service, 스프링 의존성 제거 (DI는 구성 클래스에서)
 * - 트랜잭션 경계만 남기고, 프레임워크·DB 세부 지식 없음
 */
public class ChargeWalletService {
    private final WalletRepository walletRepository;    // 도메인 포트(인터페이스)에만 의존
    
    public ChargeWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /** 지갑 충전 유스케이스 - 사용자별 비관적 락 적용 */
    @org.springframework.transaction.annotation.Transactional
    public Wallet charge(Long userId, long amount) {
        // 해당 사용자의 지갑을 가져오거나 새로 생성
        Wallet wallet = walletRepository.findByUserIdWithLock(userId)
            .orElse(Wallet.create(userId));
        
        // 도메인 로직으로 충전 처리
        wallet.charge(amount);
        
        // 저장 후 반환
        return walletRepository.save(wallet);
    }
}
