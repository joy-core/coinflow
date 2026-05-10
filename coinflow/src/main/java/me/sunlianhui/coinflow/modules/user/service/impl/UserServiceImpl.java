package me.sunlianhui.coinflow.modules.user.service.impl;

import me.sunlianhui.coinflow.common.exception.BizException;
import me.sunlianhui.coinflow.common.utils.JwtUtil;
import me.sunlianhui.coinflow.common.utils.SnowflakeIdGenerator;
import me.sunlianhui.coinflow.modules.user.entity.UserEntity;
import me.sunlianhui.coinflow.modules.user.mapper.UserMapper;
import me.sunlianhui.coinflow.modules.user.model.UserModel;
import me.sunlianhui.coinflow.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserMapper userMapper;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private SnowflakeIdGenerator snowflakeIdGenerator;

	@Override
	public UserModel getById(Long id) {
		return UserModel.fromEntity(userMapper.selectById(id));
	}

	@Override
	public UserModel getByUsername(String username) {
		UserEntity entity = userMapper.selectByUsername(username);
		return UserModel.fromEntity(entity);
	}

	@Override
	public List<UserModel> listAll() {
		return userMapper.selectList().stream()
			.map(UserModel::fromEntity)
			.toList();
	}

	@Override
	public boolean existsByUsername(String username) {
		return userMapper.selectByUsername(username) != null;
	}

	@Override
	public void register(UserModel user) {
		UserEntity entity = user.toEntity();
		entity.setId(snowflakeIdGenerator.nextId());
		System.out.println("Generated ID: " + entity.getId());
		entity.setPassword(passwordEncoder.encode(user.getPassword()));
		entity.setCreatedAt(LocalDateTime.now());
		entity.setUpdatedAt(LocalDateTime.now());
		userMapper.insert(entity);
	}

	@Override
	public void update(UserModel user) {
		UserEntity entity = user.toEntity();
		if (user.getPassword() != null) {
			entity.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		userMapper.updateById(entity);
	}

	@Override
	public void delete(Long id) {
		userMapper.deleteById(id);
	}

	@Override
	public String login(String username, String password) {
		UserEntity user = userMapper.selectByUsername(username);
		if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
			throw new BizException("Invalid username or password");
		}
		return jwtUtil.generateToken(user.getId(), user.getUsername());
	}

	@Override
	public void forgotPassword(String email) {
		UserEntity user = userMapper.selectByUsername(email);
		if (user == null) {
			throw new BizException("Email not registered");
		}
		// Simple implementation: generate reset token (should send email in production)
		String resetToken = jwtUtil.generateToken(user.getId(), user.getUsername());
		System.out.println("Password reset token: " + resetToken);
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		UserEntity user = userMapper.selectByUsername(username);
		if (user == null) {
			return false;
		}
		return passwordEncoder.matches(rawPassword, user.getPassword());
	}
}
