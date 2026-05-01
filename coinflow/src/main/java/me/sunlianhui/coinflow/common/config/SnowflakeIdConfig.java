package me.sunlianhui.coinflow.common.config;

import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeIdConfig {

	@Bean
	public SnowflakeIdGenerator snowflakeIdGenerator() {
		return new SnowflakeIdGenerator(1, 1);
	}
}
