package me.sunlianhui.coinflow.common.config;

import me.sunlianhui.coinflow.common.handler.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// Disable CSRF (applicable for frontend-backend separation)
				.csrf(csrf -> csrf.disable())

				// Do not use Session, use JWT instead
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Set API access permissions
				.authorizeHttpRequests(auth -> auth
						// Allow login, registration, captcha, documentation, etc.
						.requestMatchers(
								"/api/users/login",
								"/api/users/register",   // Enable as needed
								"/api/users/forgot-password",
								"/captcha",
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/api/categories/**"
						).permitAll()

						// All other endpoints require authentication
						.anyRequest().authenticated()
				).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		;

		return http.build();
	}

	// Add this if you need password verification and login logic
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManager.class);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
