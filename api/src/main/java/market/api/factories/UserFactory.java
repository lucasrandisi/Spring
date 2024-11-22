package market.api.factories;

import market.api.models.Role;
import market.api.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class UserFactory extends Factory {
	private final PasswordEncoder passwordEncoder;
	private final RoleFactory roleFactory;

	public UserFactory(PasswordEncoder passwordEncoder, RoleFactory roleFactory) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.roleFactory = roleFactory;
	}

	public UserBuilder builder() {
		return new UserBuilder();
	}

	public class UserBuilder {
		private String firstName;
		private String lastName;
		private String email;
		private String password;

		public UserBuilder firstname(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public UserBuilder lastname(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public UserBuilder email(String email) {
			this.email = email;
			return this;
		}

		public UserBuilder password(String password) {
			this.password = password;
			return this;
		}

		public User build() {
			User user = new User();
			user.setFirstName(firstName != null ? firstName : faker.name().firstName());
			user.setLastName(lastName != null ? lastName : faker.name().lastName());
			user.setEmail(email != null ? email : faker.internet().emailAddress());
			user.setPassword(password != null ? passwordEncoder.encode(password) : passwordEncoder.encode(faker.internet().password()));

			Role role = roleFactory.builder().build();
			user.setRoles(Set.of(role));

			return user;
		}
	}
}
