package market.api.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import market.api.repositories.BlackListedTokenRepository;
import market.api.utils.JwtUtil;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	private final HandlerExceptionResolver handlerExceptionResolver;
	private final BlackListedTokenRepository blacklistedTokenRepository;


	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver, BlackListedTokenRepository blacklistedTokenRepository) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.handlerExceptionResolver = handlerExceptionResolver;
		this.blacklistedTokenRepository = blacklistedTokenRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			final String jwt = authHeader.substring(7);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null && jwtUtil.isTokenValid(jwt)) {
				// Check if the token has not been blacklisted
				if (blacklistedTokenRepository.existsByToken(jwt)) {
					filterChain.doFilter(request, response);
					return;
				}

				final String userEmail = jwtUtil.extractUsername(jwt);
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);


				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}

			filterChain.doFilter(request, response);
		} catch (Exception exception) {
			handlerExceptionResolver.resolveException(request, response, null, exception);
		}
	}
}