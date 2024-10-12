package market.api.controllers;

import market.api.dtos.auth.LoginRequestDTO;
import market.api.dtos.auth.LoginResponseDTO;
import market.api.dtos.auth.RegisterRequestDTO;
import market.api.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<LoginResponseDTO> register(@RequestBody @Validated RegisterRequestDTO registerRequestDTO) {
		LoginResponseDTO loginResponseDto = authService.register(registerRequestDTO);

		return ResponseEntity.ok(loginResponseDto);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody @Validated LoginRequestDTO loginRequestDTO) {
		LoginResponseDTO loginResponseDto = authService.login(loginRequestDTO);

		return ResponseEntity.ok(loginResponseDto);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader("Authorization") String jwt) {
		jwt = jwt.substring(7);

		authService.logout(jwt);
		return ResponseEntity.noContent().build();
	}
}
