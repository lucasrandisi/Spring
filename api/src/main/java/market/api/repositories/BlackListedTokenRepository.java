package market.api.repositories;

import market.api.models.BlackListedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BlackListedTokenRepository extends JpaRepository<BlackListedToken, Long> {
	Boolean existsByToken(String token);

	@Modifying
	long deleteByExpireDateLessThan(LocalDateTime localDateTime);
}
