package me.sunlianhui.coinflow.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import me.sunlianhui.coinflow.common.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

	@Autowired
	private JwtProperties jwtProperties;

	private SecretKey key;

	/**
	 * Initialize SecretKey after reading configuration, Base64 decode and validate key length
	 */
	@PostConstruct
	public void init() {
		try {
			byte[] decodedKey = Base64.getDecoder().decode(jwtProperties.getSecret());
			if (decodedKey.length < 32) {
				throw new IllegalArgumentException("JWT secret key length is less than 256 bits");
			}
			this.key = Keys.hmacShaKeyFor(decodedKey);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid JWT secret key. Please provide a Base64 encoded string with at least 256 bits", e);
		}
	}

	/**
	 * Generate token with expiration based on configuration
	 */
	public String generateToken(Long userId, String username) {
		return Jwts.builder()
				.setSubject(userId + "," + username)
				.setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpire()))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Parse token
	 */
	public Claims parseToken(String token) {
		byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
		SecretKey key = Keys.hmacShaKeyFor(keyBytes);
		JwtParserBuilder parserBuilder = Jwts.parser();  // Note: use parser() here, not parserBuilder()
		return parserBuilder
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * Get user ID from token
	 */
	public Long getUserId(String token) {
		String subject = parseToken(token).getSubject();
		return Long.parseLong(subject.split(",")[0]);
	}
}
