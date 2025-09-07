package server.interfaces.wallet;

import lombok.Data;
import server.domain.wallet.Wallet;
import java.time.LocalDateTime;

@Data
public class WalletResponse {
    private Long userId;
    private long balance;
    private LocalDateTime updatedAt;

    public static WalletResponse from(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.userId = wallet.getUserId();
        response.balance = wallet.getBalance();
        response.updatedAt = wallet.getUpdatedAt();
        return response;
    }
}
