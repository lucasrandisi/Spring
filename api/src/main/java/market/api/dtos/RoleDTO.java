package market.api.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import market.api.models.Role;

@NoArgsConstructor
@Getter
@Setter
public class RoleDTO {
	private Integer id;
	private String name;

	public RoleDTO(Role role) {
		this.id = role.getId();
		this.name = role.getName();
	}
}
