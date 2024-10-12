package market.api.factories;

import com.github.javafaker.Faker;
import market.api.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class UserFactory {
	private final Faker faker;
	private final PasswordEncoder passwordEncoder;

	public UserFactory(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
		this.faker = new Faker();
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
			return user;
		}
	}
}
