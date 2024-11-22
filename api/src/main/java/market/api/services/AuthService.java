package market.api.services;

import market.api.dtos.UserDTO;
import market.api.dtos.auth.LoginRequestDTO;
import market.api.dtos.auth.LoginResponseDTO;
import market.api.dtos.auth.RegisterRequestDTO;
import market.api.enums.RoleEnum;
import market.api.exceptions.ConflictException;
import market.api.models.BlackListedToken;
import market.api.models.Role;
import market.api.models.User;
import market.api.repositories.BlackListedTokenRepository;
import market.api.repositories.RoleRepository;
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

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final BlackListedTokenRepository blackListedTokenRepository;

	public AuthService(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, BlackListedTokenRepository blackListedTokenRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
		this.blackListedTokenRepository = blackListedTokenRepository;
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

		Role role = roleRepository.findById(RoleEnum.USER.getId()).orElseThrow();

		user.getRoles().add(role);

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
		UserDTO userDTO = new UserDTO(user);

		LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
		loginResponseDTO.setUser(userDTO);
		loginResponseDTO.setToken(jwt);
		loginResponseDTO.setExpiresIn(jwtUtil.getExpirationTime());

		return loginResponseDTO;
	}

	public void logout(String jwt) {
		LocalDateTime expiration = jwtUtil.extractExpiration(jwt);

		BlackListedToken blacklistedToken = new BlackListedToken();
		blacklistedToken.setToken(jwt);
		blacklistedToken.setExpireDate(expiration);

		blackListedTokenRepository.save(blacklistedToken);
	}
}
