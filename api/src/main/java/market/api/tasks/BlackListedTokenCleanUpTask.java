package market.api.tasks;

import market.api.repositories.BlackListedTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class BlackListedTokenCleanUpTask {
	private static final Logger logger = LoggerFactory.getLogger(BlackListedTokenCleanUpTask.class);
	private final BlackListedTokenRepository blackListedTokenRepository;

	public BlackListedTokenCleanUpTask(BlackListedTokenRepository blackListedTokenRepository) {
		this.blackListedTokenRepository = blackListedTokenRepository;
	}

	@Scheduled(cron = "0 0 2 * * *")
	@Transactional
	public void deleteExpiredBlacklistedTokens() {
		long deletedCount = blackListedTokenRepository.deleteByExpireDateLessThan(LocalDateTime.now());
		logger.info("Deleted {} expired blacklisted tokens", deletedCount);
	}
}
