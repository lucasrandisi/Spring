package market.api.unit.auth;

import market.api.dtos.UserDTO;
import market.api.dtos.auth.LoginRequestDTO;
import market.api.dtos.auth.LoginResponseDTO;
import market.api.models.User;
import market.api.services.AuthService;
import market.api.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginTest {
	private static final String EMAIL = "email@test.com";
	private static final String PASSWORD = "password";
	private static final String TOKEN = "token";

	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	JwtUtil jwtUtil;

	@InjectMocks
	AuthService authService;

	private LoginRequestDTO loginRequestDTO;
	private User user;

	@BeforeEach
	public void setUp() {
		loginRequestDTO = new LoginRequestDTO();
		loginRequestDTO.setEmail(EMAIL);
		loginRequestDTO.setPassword(PASSWORD);

		user = new User();
		user.setEmail(EMAIL);
		user.setPassword(PASSWORD);
		user.setFirstName("firstName");
		user.setLastName("lastName");
	}

	@Test
	public void throwsExceptionWhenWrongCredentials() {
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Authentication failed") {});

		BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authService.login(loginRequestDTO));
		assertEquals(exception.getMessage(), "Incorrect email or password");
	}

	@Test
	public void successfullyLogin() {
		Authentication authentication = mock(Authentication.class);

		when(authentication.getPrincipal()).thenReturn(user);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
		when(jwtUtil.generateToken(any())).thenReturn(TOKEN);

		UserDTO userDto = new UserDTO();
		userDto.setEmail(EMAIL);
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());

		LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
		loginResponseDTO.setUser(userDto);
		loginResponseDTO.setToken(TOKEN);

		assertEquals(authService.login(loginRequestDTO), loginResponseDTO);
	}
}
