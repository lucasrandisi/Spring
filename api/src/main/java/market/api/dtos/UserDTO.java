package market.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import market.api.models.User;
import org.hibernate.Hibernate;

import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
	private Integer id;
	private String email;
	private String firstName;
	private String lastName;
	private Set<RoleDTO> roles;

	public UserDTO(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();

		if (Hibernate.isInitialized(user.getRoles())) {
			this.roles = user.getRoles().stream()
				.map(RoleDTO::new)
				.collect(Collectors.toSet());
		}
	}
}
