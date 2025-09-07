package server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import server.application.order.PlaceOrderService;
import server.application.wallet.ChargeWalletService;
import server.application.wallet.GetWalletBalanceService;
import server.domain.order.OrderRepository;
import server.domain.wallet.WalletRepository;

@Configuration
public class ApplicationConfig {
    
    @Bean
    public PlaceOrderService placeOrderService(OrderRepository orderRepository, WalletRepository walletRepository) {
        return new PlaceOrderService(orderRepository, walletRepository);
    }
    
    @Bean
    public ChargeWalletService chargeWalletService(WalletRepository walletRepository) {
        return new ChargeWalletService(walletRepository);
    }
    
    @Bean
    public GetWalletBalanceService getWalletBalanceService(WalletRepository walletRepository) {
        return new GetWalletBalanceService(walletRepository);
    }
}