	package market.api.config;

	import market.api.filters.JwtAuthenticationFilter;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.http.HttpStatus;
	import org.springframework.security.authentication.AuthenticationManager;
	import org.springframework.security.authentication.ProviderManager;
	import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
	import org.springframework.security.config.annotation.web.builders.HttpSecurity;
	import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
	import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
	import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
	import org.springframework.security.config.http.SessionCreationPolicy;
	import org.springframework.security.core.userdetails.UserDetailsService;
	import org.springframework.security.crypto.factory.PasswordEncoderFactories;
	import org.springframework.security.crypto.password.PasswordEncoder;
	import org.springframework.security.web.SecurityFilterChain;
	import org.springframework.security.web.authentication.HttpStatusEntryPoint;
	import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


	@Configuration
	@EnableWebSecurity
	public class SecurityConfig {
		private final JwtAuthenticationFilter jwtAuthenticationFilter;

		public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
			this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		}

		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers("/auth/login").permitAll()
					.requestMatchers("/auth/register").permitAll()
					.requestMatchers("/test/public").permitAll()
					.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.requestCache(RequestCacheConfigurer::disable)
				.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(configurer ->
					configurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				);

			return http.build();
		}

		@Bean
		public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
			DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
			authenticationProvider.setUserDetailsService(userDetailsService);
			authenticationProvider.setPasswordEncoder(passwordEncoder);

			return new ProviderManager(authenticationProvider);
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		}

	}