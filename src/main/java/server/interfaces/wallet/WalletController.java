package server.interfaces.wallet;

import org.springframework.web.bind.annotation.*;
import server.application.wallet.ChargeWalletService;
import server.application.wallet.GetWalletBalanceService;
import server.domain.wallet.Wallet;

@RestController
@RequestMapping("/v1/wallets")
public class WalletController {

    private final ChargeWalletService chargeWalletService;
    private final GetWalletBalanceService getWalletBalanceService;

    public WalletController(ChargeWalletService chargeWalletService, GetWalletBalanceService getWalletBalanceService) {
        this.chargeWalletService = chargeWalletService;
        this.getWalletBalanceService = getWalletBalanceService;
    }

    @PostMapping("/{userId}/charge")
    public WalletResponse chargeWallet(@PathVariable Long userId, @RequestBody WalletRequest request) {
        Wallet wallet = chargeWalletService.charge(userId, request.getAmount());
        return WalletResponse.from(wallet);
    }

    @GetMapping("/{userId}")
    public WalletResponse getBalance(@PathVariable Long userId) {
        Wallet wallet = getWalletBalanceService.getBalance(userId);
        return WalletResponse.from(wallet);
    }
}

