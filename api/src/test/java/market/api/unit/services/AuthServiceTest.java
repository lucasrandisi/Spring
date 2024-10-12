package market.api.unit.services;

import market.api.dtos.auth.LoginRequestDTO;
import market.api.dtos.auth.RegisterRequestDTO;
import market.api.exceptions.ConflictException;
import market.api.models.User;
import market.api.repositories.UserRepository;
import market.api.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	AuthService authService;

	@Test
	public void login_throwsExceptionWhenUsingWrongCredentials() {
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Authentication failed") {});

		LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
		loginRequestDTO.setEmail("email@test.com");
		loginRequestDTO.setPassword("password");

		BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDTO));
		assertEquals(exception.getMessage(), "Incorrect email or password");
	}

	@Test
	void register_throwsConflictExceptionOnAlreadyExistingEmail() {
		RegisterRequestDTO registerRequestDTO= new RegisterRequestDTO();
		registerRequestDTO.setEmail("email@email.com");
		registerRequestDTO.setPassword("password");
		registerRequestDTO.setFirstName("firstName");
		registerRequestDTO.setLastName("lastName");

		when(userRepository.findByEmail(registerRequestDTO.getEmail())).thenReturn(Optional.of(new User()));

		ConflictException exception = assertThrows(ConflictException.class, () -> authService.register(registerRequestDTO));
		assertEquals("Email already exists", exception.getMessage());
	}
}
