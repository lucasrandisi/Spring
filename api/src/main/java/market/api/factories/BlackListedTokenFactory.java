package market.api.factories;

import market.api.models.BlackListedToken;
import market.api.models.User;
import market.api.utils.JwtUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BlackListedTokenFactory {
	private final JwtUtil jwtUtil;
	private final UserFactory userFactory;

	public BlackListedTokenFactory(JwtUtil jwtUtil, UserFactory userFactory) {
		this.jwtUtil = jwtUtil;
		this.userFactory = userFactory;
	}

	public BlackListedTokenBuilder builder() {
		return new BlackListedTokenBuilder();
	}

	public String generateJwt() {
		User user = userFactory.builder().build();

		return jwtUtil.generateToken(user);
	}

	public class BlackListedTokenBuilder {
		private String token;
		private LocalDateTime expireDate;

		public BlackListedTokenBuilder setToken(String token) {
			this.token = token;
			return this;
		}

		public BlackListedTokenBuilder setExpireDate(LocalDateTime expireDate) {
			this.expireDate = expireDate;
			return this;
		}

		public BlackListedToken build() {
			BlackListedToken blackListedToken = new BlackListedToken();
			blackListedToken.setToken(token != null ? token : generateJwt());
			blackListedToken.setExpireDate(expireDate != null ? expireDate : LocalDateTime.now().plusHours(2));

			return blackListedToken;
		}
	}
}
