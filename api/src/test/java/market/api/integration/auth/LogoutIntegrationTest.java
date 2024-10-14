package market.api.integration.auth;

import market.api.factories.BlackListedTokenFactory;
import market.api.factories.UserFactory;
import market.api.models.BlackListedToken;
import market.api.models.User;
import market.api.repositories.BlackListedTokenRepository;
import market.api.repositories.UserRepository;
import market.api.tasks.BlackListedTokenCleanUpTask;
import market.api.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LogoutIntegrationTest {
	@Autowired
	UserFactory userFactory;
	@Autowired
	BlackListedTokenFactory blacklistedTokenFactory;
	@Autowired
	BlackListedTokenCleanUpTask blacklistedTokenCleanUpTask;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BlackListedTokenRepository blacklistedTokenRepository;
	@Autowired
	private JwtUtil jwtUtil;

	@Test
	public void testSuccessfullyLogout() throws Exception {
		User user = userFactory.builder().build();
		userRepository.save(user);

		String token = jwtUtil.generateToken(user);

		mockMvc.perform(post("/auth/logout")
			.header("Authorization", "Bearer " + token)
		).andExpect(status().isNoContent());

		mockMvc.perform(post("/test/protected")
			.header("Authorization", "Bearer " + token)
		).andExpect(status().isUnauthorized());

		assert (blacklistedTokenRepository.existsByToken(token));
	}

	@Test
	public void testBlacklistedTokenCleanup() {
		BlackListedToken nonExpiredBlackListedToken = blacklistedTokenFactory.builder()
			.setExpireDate(LocalDateTime.now().plusHours(1))
			.build();

		BlackListedToken expiredBlackListedToken = blacklistedTokenFactory.builder()
			.setExpireDate(LocalDateTime.now().minusHours(1))
			.build();

		blacklistedTokenRepository.saveAll(List.of(nonExpiredBlackListedToken, expiredBlackListedToken));

		blacklistedTokenCleanUpTask.deleteExpiredBlacklistedTokens();

		assertTrue(blacklistedTokenRepository.existsByToken(nonExpiredBlackListedToken.getToken()));
		assertFalse(blacklistedTokenRepository.existsByToken(expiredBlackListedToken.getToken()));
	}
}
