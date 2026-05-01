package me.sunlianhui.coinflow.modules.user.service;

import me.sunlianhui.coinflow.modules.user.model.UserModel;

import java.util.List;

public interface UserService {
	UserModel getById(Long id);

	UserModel getByUsername(String username);

	List<UserModel> listAll();

	boolean existsByUsername(String username);

	void register(UserModel user);

	void update(UserModel user);

	void delete(Long id);

	String login(String username, String password);

	void forgotPassword(String email);
}
