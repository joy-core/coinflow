package me.sunlianhui.coinflow.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	/**
	 * Base64 encoded secret key, at least 32 bytes (256 bits)
	 */
	private String secret;

	/**
	 * Expiration time in milliseconds
	 */
	private long expire;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}
}
