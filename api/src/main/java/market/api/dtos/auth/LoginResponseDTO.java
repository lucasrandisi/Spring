package market.api.dtos.auth;

import lombok.Data;
import market.api.dtos.UserDTO;

@Data
public class LoginResponseDTO {
	private String token;
	private long expiresIn;
	private UserDTO user;
}
