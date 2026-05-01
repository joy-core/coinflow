package me.sunlianhui.coinflow.common.handler;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.sunlianhui.coinflow.common.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	@Autowired
	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			try {
				Claims claims = jwtUtil.parseToken(token);
				String subject = claims.getSubject(); // userId,username
				if (StringUtils.hasText(subject)) {
					String[] parts = subject.split(",");
					String userId = parts[0];

					UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(userId, null, null);

					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (Exception e) {
				// Invalid token, ignore
			}
		}
		filterChain.doFilter(request, response);
	}
}

