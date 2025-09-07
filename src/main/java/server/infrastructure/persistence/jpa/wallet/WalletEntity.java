package server.infrastructure.persistence.jpa.wallet;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="wallets")
@Data
class WalletEntity {
    @Id
    @Column(name = "user_id")
    Long userId;
    
    Long balance;
    
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
