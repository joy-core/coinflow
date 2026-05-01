package me.sunlianhui.coinflow.modules.user.model;

import lombok.Data;
import me.sunlianhui.coinflow.modules.user.entity.UserEntity;

import java.time.LocalDateTime;

@Data
public class UserModel {
	private Long id;          // Snowflake ID
	private String username;
	private String password;
	private String email;
	private String phone;
	private String avatar;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// Entity -> Model (static factory method)
	public static UserModel fromEntity(UserEntity entity) {
		if (entity == null) {
			return null;
		}
		UserModel model = new UserModel();
		model.setId(entity.getId());
		model.setUsername(entity.getUsername());
		model.setEmail(entity.getEmail());
		model.setPhone(entity.getPhone());
		model.setAvatar(entity.getAvatar());
		model.setCreatedAt(entity.getCreatedAt());
		model.setUpdatedAt(entity.getUpdatedAt());
		return model;
	}

	// Model -> Entity
	public UserEntity toEntity() {
		UserEntity entity = new UserEntity();
		entity.setUsername(this.username);
		entity.setEmail(this.email);
		entity.setPhone(this.phone);
		entity.setPassword(this.password);
		entity.setAvatar(this.avatar);
		return entity;
	}
}
