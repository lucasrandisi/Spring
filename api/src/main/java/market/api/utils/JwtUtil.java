package market.api.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
	@Value("${security.jwt.secret-key}")
	private String secretKey;

	@Value("${security.jwt.expiration-time}")
	private long jwtExpiration;

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", userDetails.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.toCollection(HashSet::new))
		);

		return buildToken(claims, userDetails, jwtExpiration);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public LocalDateTime extractExpiration(String token) {
		Date expirationDate = extractClaim(token, Claims::getExpiration);
		return LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
	}

	public long getExpirationTime() {
		return jwtExpiration;
	}

	public boolean isTokenValid(String token) {
		return extractClaim(token, Claims::getExpiration).after(new Date());
	}

	private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
		return Jwts
			.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(getSignInKey(), SignatureAlgorithm.HS256)
			.compact();
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts
			.parserBuilder()
			.setSigningKey(getSignInKey())
			.build()
			.parseClaimsJws(token)
			.getBody();

		return claimsResolver.apply(claims);
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}