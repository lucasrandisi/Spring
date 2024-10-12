package market.api.integration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import market.api.dtos.auth.LoginRequestDTO;
import market.api.dtos.auth.LoginResponseDTO;
import market.api.factories.UserFactory;
import market.api.models.User;
import market.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LoginIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Value("${security.jwt.expiration-time}")
	private long jwtExpiration;
	@Autowired
	private UserFactory userFactory;

	@Test
	public void testSuccessfulLogin() throws Exception {
		String email = "test@example.com";
		String password = "password123";

		User user = userFactory.builder()
			.email(email)
			.password(password)
			.build();

		userRepository.save(user);

		LoginRequestDTO loginRequest = new LoginRequestDTO();
		loginRequest.setEmail(email);
		loginRequest.setPassword(password);

		MvcResult loginResult = mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
			.andExpect(status().isOk())
			.andReturn();

		String content = loginResult.getResponse().getContentAsString();
		LoginResponseDTO loginResponseDTO = objectMapper.readValue(content, LoginResponseDTO.class);

		assertNotNull(loginResponseDTO.getToken());
		assertEquals(loginResponseDTO.getExpiresIn(), jwtExpiration);
		assertEquals(user.getEmail(), loginResponseDTO.getUser().getEmail());
		assertEquals(user.getFirstName(), loginResponseDTO.getUser().getFirstName());
		assertEquals(user.getLastName(), loginResponseDTO.getUser().getLastName());

		mockMvc.perform(get("/test/protected")
				.header("Authorization", "Bearer " + loginResponseDTO.getToken())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}
}
