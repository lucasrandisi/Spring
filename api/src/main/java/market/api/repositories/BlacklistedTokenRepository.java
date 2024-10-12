package market.api.repositories;

import market.api.models.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
	Boolean existsByToken(String token);
}
