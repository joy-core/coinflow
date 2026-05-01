package me.sunlianhui.coinflow.modules.user.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserEntity {
	private Long id;          // Snowflake ID
	private String username;
	private String email;
	private String phone;
	private String password;
	private String avatar;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
