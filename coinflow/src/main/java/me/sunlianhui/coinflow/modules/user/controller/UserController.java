package me.sunlianhui.coinflow.modules.user.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.common.utils.JwtUtil;
import me.sunlianhui.coinflow.modules.user.model.LoginRequest;
import me.sunlianhui.coinflow.modules.user.model.LoginResponse;
import me.sunlianhui.coinflow.modules.user.model.UserModel;
import me.sunlianhui.coinflow.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping("/{id}")
	public R<UserModel> get(@PathVariable Long id) {
		return R.success(userService.getById(id));
	}

	@GetMapping
	public R<List<UserModel>> list() {
		return R.success(userService.listAll());
	}

	@PostMapping("/register")
	public R<Void> register(@RequestBody UserModel user) {
		if (userService.existsByUsername(user.getUsername())) {
			return R.fail("Username already exists");
		}
		userService.register(user);
		return R.success();
	}

	@PutMapping
	public R<Void> update(@RequestBody UserModel user) {
		userService.update(user);
		return R.success();
	}

	@DeleteMapping("/{id}")
	public R<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return R.success();
	}

	@PostMapping("/login")
	public R<LoginResponse> login(@RequestBody LoginRequest req) {
		String token = userService.login(req.getUsername(), req.getPassword());
		LoginResponse resp = new LoginResponse();
		resp.setToken(token);
		resp.setUsername(req.getUsername());
		return R.success(resp);
	}

	@PostMapping("/forgot-password")
	public R<Void> forgotPassword(@RequestBody java.util.Map<String, String> request) {
		userService.forgotPassword(request.get("email"));
		return R.success();
	}

	@PostMapping("/change-password")
	public R<Void> changePassword(@RequestBody Map<String, String> request) {
		String userIdStr = request.get("userId");
		String oldPassword = request.get("oldPassword");
		String newPassword = request.get("newPassword");
		if (userIdStr == null || oldPassword == null || newPassword == null) {
			return R.fail("Missing required fields");
		}
		Long userId = Long.parseLong(userIdStr);
		UserModel user = userService.getById(userId);
		if (user == null) {
			return R.fail("User not found");
		}
		if (!userService.verifyPassword(user.getUsername(), oldPassword)) {
			return R.fail("Old password is incorrect");
		}
		user.setPassword(newPassword);
		userService.update(user);
		return R.success();
	}

	@PostMapping("/reset-password")
	public R<Void> resetPassword(@RequestBody Map<String, String> request) {
		String token = request.get("token");
		String newPassword = request.get("newPassword");
		if (token == null || newPassword == null) {
			return R.fail("Missing required fields");
		}
		Long userId = jwtUtil.getUserIdFromToken(token);
		if (userId == null) {
			return R.fail("Invalid token");
		}
		UserModel user = userService.getById(userId);
		if (user == null) {
			return R.fail("User not found");
		}
		user.setPassword(newPassword);
		userService.update(user);
		return R.success();
	}
}
