package market.api.services;

import market.api.dtos.auth.LoginRequestDTO;
import market.api.dtos.auth.LoginResponseDTO;
import market.api.dtos.auth.RegisterRequestDTO;
import market.api.dtos.UserDTO;
import market.api.exceptions.ConflictException;
import market.api.models.BlacklistedToken;
import market.api.models.User;
import market.api.repositories.BlacklistedTokenRepository;
import market.api.repositories.UserRepository;
import market.api.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final BlacklistedTokenRepository blacklistedTokenRepository;

	public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, BlacklistedTokenRepository blacklistedTokenRepository) {
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
		this.blacklistedTokenRepository = blacklistedTokenRepository;
	}

	public LoginResponseDTO register(RegisterRequestDTO registerRequestDTO) {
		if (userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
			throw new ConflictException("Email already exists");
		}

		User user = new User();
		user.setEmail(registerRequestDTO.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
		user.setFirstName(registerRequestDTO.getFirstName());
		user.setLastName(registerRequestDTO.getLastName());
		user.setCreatedAt(LocalDateTime.now());

		userRepository.save(user);

		String jwt = jwtUtil.generateToken(user);

		return buildLoginResponseDTO(user, jwt);
	}


	public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
		Authentication authentication;

		try {
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
		} catch (AuthenticationException e) {
			throw new BadCredentialsException("Incorrect email or password", e);
		}

		User user = (User) authentication.getPrincipal();

		String jwt = jwtUtil.generateToken(user);

		return buildLoginResponseDTO(user, jwt);
	}

	private LoginResponseDTO buildLoginResponseDTO(User user, String jwt) {
		UserDTO userDTO = new UserDTO();
		userDTO.setEmail(user.getEmail());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());

		LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
		loginResponseDTO.setUser(userDTO);
		loginResponseDTO.setToken(jwt);
		loginResponseDTO.setExpiresIn(jwtUtil.getExpirationTime());

		return loginResponseDTO;
	}

	public void logout(String jwt) {
		Date expiration = jwtUtil.extractExpiration(jwt);

		BlacklistedToken blacklistedToken = new BlacklistedToken();
		blacklistedToken.setToken(jwt);
		blacklistedToken.setExpireDate(expiration);

		blacklistedTokenRepository.save(blacklistedToken);
	}
}
