package market.api.dtos.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDTO {
	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String password;
	@NotBlank
	@JsonProperty("first_name")
	private String firstName;
	@NotBlank
	@JsonProperty("last_name")
	private String lastName;
}
