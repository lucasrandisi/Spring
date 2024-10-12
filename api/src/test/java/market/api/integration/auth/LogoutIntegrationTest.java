package market.api.integration.auth;

import market.api.factories.UserFactory;
import market.api.models.User;
import market.api.repositories.BlacklistedTokenRepository;
import market.api.repositories.UserRepository;
import market.api.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LogoutIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BlacklistedTokenRepository blacklistedTokenRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	UserFactory userFactory;

	@Test
	public void testSuccessfullyLogout() throws Exception {
		User user =  userFactory.builder().build();
		userRepository.save(user);

		String token = jwtUtil.generateToken(user);

		mockMvc.perform(post("/auth/logout")
				.header("Authorization", "Bearer " + token)
			).andExpect(status().isNoContent());

		mockMvc.perform(post("/test/protected")
				.header("Authorization", "Bearer " + token)
			).andExpect(status().isUnauthorized());

		assert(blacklistedTokenRepository.existsByToken(token));
	}
}
