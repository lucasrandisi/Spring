package market.api.unit.auth;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AuthService authService;

	@Test
	void throwsExceptionWhenExistingEmail() {
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