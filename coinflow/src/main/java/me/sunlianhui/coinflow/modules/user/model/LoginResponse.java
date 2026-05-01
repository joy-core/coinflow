package me.sunlianhui.coinflow.modules.user.model;

import lombok.Data;

@Data
public class LoginResponse {
	private String token;
	private String username;
}
