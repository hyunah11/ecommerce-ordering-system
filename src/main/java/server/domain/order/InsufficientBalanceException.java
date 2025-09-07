package server.domain.order;

public class InsufficientBalanceException extends RuntimeException {
    private final long requiredAmount;
    private final long currentBalance;

    public InsufficientBalanceException(long requiredAmount, long currentBalance) {
        super(String.format("잔액이 부족합니다. 필요 금액: %d원, 현재 잔액: %d원", requiredAmount, currentBalance));
        this.requiredAmount = requiredAmount;
        this.currentBalance = currentBalance;
    }

    public long getRequiredAmount() {
        return requiredAmount;
    }

    public long getCurrentBalance() {
        return currentBalance;
    }
}