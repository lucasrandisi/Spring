package market.api.integration.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import market.api.dtos.auth.LoginResponseDTO;
import market.api.dtos.auth.RegisterRequestDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class RegistrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Value("${security.jwt.expiration-time}")
	private long jwtExpiration;

	@Test
	public void testSuccessfulRegistration() throws Exception {
		String email = "test@example.com";
		String password = "password123";
		String firstName = "John";
		String lastName = "Doe";

		RegisterRequestDTO registerRequest = new RegisterRequestDTO();
		registerRequest.setEmail(email);
		registerRequest.setPassword(password);
		registerRequest.setFirstName(firstName);
		registerRequest.setLastName(lastName);

		MvcResult registrationResult = mockMvc.perform(post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)))
			.andExpect(status().isOk())
			.andReturn();

		String content = registrationResult.getResponse().getContentAsString();
		LoginResponseDTO loginResponseDTO = objectMapper.readValue(content, LoginResponseDTO.class);

		assertNotNull(loginResponseDTO.getToken());
		assertEquals(loginResponseDTO.getExpiresIn(), jwtExpiration);
		assertEquals(email, loginResponseDTO.getUser().getEmail());
		assertEquals(firstName, loginResponseDTO.getUser().getFirstName());
		assertEquals(lastName, loginResponseDTO.getUser().getLastName());

		assertTrue(userRepository.findByEmail(email).isPresent());

		mockMvc.perform(get("/test/protected")
				.header("Authorization", "Bearer " + loginResponseDTO.getToken())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
	}
}
