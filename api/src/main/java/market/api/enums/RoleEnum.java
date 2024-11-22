package market.api.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
	ADMIN(1, "Admin"),
	USER(2, "User");

	private final Integer id;
	private final String name;

	RoleEnum(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
}
