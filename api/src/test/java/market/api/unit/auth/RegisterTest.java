package market.api.unit.auth;

import market.api.dtos.UserDTO;
import market.api.dtos.auth.LoginResponseDTO;
import market.api.dtos.auth.RegisterRequestDTO;
import market.api.exceptions.ConflictException;
import market.api.models.User;
import market.api.repositories.UserRepository;
import market.api.services.AuthService;
import market.api.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	JwtUtil jwtUtil;

	@InjectMocks
	private AuthService authService;


	@Test
	void throwsExceptionWhenExistingEmail() {
		RegisterRequestDTO registerRequestDTO = this.buildRegisterRequestDTO();

		when(userRepository.findByEmail(registerRequestDTO.getEmail())).thenReturn(Optional.of(new User()));

		ConflictException exception = assertThrows(ConflictException.class, () -> authService.register(registerRequestDTO));
		assertEquals("Email already exists", exception.getMessage());
	}

	@Test
	void successfullyRegisterUser() {
		String token = "1234";
		String encryptedPassword = "encryptedPassword";

		RegisterRequestDTO registerRequestDTO = this.buildRegisterRequestDTO();

		User user = new User();
		user.setEmail(registerRequestDTO.getEmail());
		user.setPassword(encryptedPassword);
		user.setFirstName(registerRequestDTO.getFirstName());
		user.setLastName(registerRequestDTO.getLastName());
		user.setCreatedAt(LocalDateTime.now());

		UserDTO userDTO = new UserDTO();
		userDTO.setEmail(registerRequestDTO.getEmail());
		userDTO.setFirstName(registerRequestDTO.getFirstName());
		userDTO.setLastName(registerRequestDTO.getLastName());

		when(userRepository.findByEmail(registerRequestDTO.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn(encryptedPassword);
		when(jwtUtil.generateToken(any(User.class))).thenReturn(token);

		LoginResponseDTO response = authService.register(registerRequestDTO);
		LoginResponseDTO expected = new LoginResponseDTO();
		expected.setToken(token);
		expected.setUser(userDTO);

		assertEquals(expected, response);
	}

	private RegisterRequestDTO buildRegisterRequestDTO() {
		RegisterRequestDTO registerRequestDTO= new RegisterRequestDTO();
		registerRequestDTO.setEmail("email@email.com");
		registerRequestDTO.setPassword("password");
		registerRequestDTO.setFirstName("firstName");
		registerRequestDTO.setLastName("lastName");

		return registerRequestDTO;
	}
}