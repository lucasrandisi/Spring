package market.api.unit.auth;

import market.api.dtos.auth.LoginRequestDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginTest {
	@Mock
	AuthenticationManager authenticationManager;

	@InjectMocks
	AuthService authService;

	@Test
	public void throwsExceptionWhenWrongCredentials() {
		LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
		loginRequestDTO.setEmail("email@test.com");
		loginRequestDTO.setPassword("password");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Authentication failed") {});

		BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDTO));
		assertEquals(exception.getMessage(), "Incorrect email or password");
	}
}
