package me.sunlianhui.coinflow.modules.user.controller;

import me.sunlianhui.coinflow.common.web.R;
import me.sunlianhui.coinflow.modules.user.model.LoginRequest;
import me.sunlianhui.coinflow.modules.user.model.LoginResponse;
import me.sunlianhui.coinflow.modules.user.model.UserModel;
import me.sunlianhui.coinflow.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;

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
}
