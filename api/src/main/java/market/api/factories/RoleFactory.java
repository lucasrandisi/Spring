package market.api.factories;

import market.api.enums.RoleEnum;
import market.api.models.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleFactory extends Factory {
	public RoleBuilder builder() {
		return new RoleBuilder();
	}

	public class RoleBuilder {
		private Integer id;
		private String name;

		public RoleBuilder setId(Integer id) {
			this.id = id;
			return this;
		}

		public RoleBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public Role build() {
			Role role = new Role();

			role.setId(id != null ? id : RoleEnum.USER.getId());
			role.setName(name != null ? name : RoleEnum.USER.getName());

			return role;
		}
	}
}
