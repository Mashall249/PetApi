package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	// ユーザー参照
	@GetMapping("/{username}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> findByUsername(@PathVariable String username, Authentication authentication) {
		return userService.get(username, authentication);
	}
	
	// 登録
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse registerUser(@RequestBody UserRequest userRequest) {
		return userService.post(userRequest);
	}
	
	// ユーザー更新
	@PutMapping("/{username}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserRequest userRequest, Authentication authentication) {
		return userService.update(username, userRequest, authentication);
	}

	// ユーザー削除
	@DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> deleteUser(@PathVariable String username, Authentication authentication) {
		return userService.delete(username, authentication);
	}
	
	// ログイン処理
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		return userService.login(loginRequest);
	}
	
	// ログアウト処理
	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(HttpServletRequest request, Authentication authentication) {
		return userService.logout(request, authentication);
	}
}
