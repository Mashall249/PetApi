package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserRequest;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	// ユーザー参照
	@GetMapping("/{username}")
	public ResponseEntity<?> findUserByUsername(@PathVariable String username, Authentication authentication) {
		return ResponseEntity.ok(userService.get(username, authentication));
	}
	
	// 登録
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userRequest));
	}
	
	// ユーザー更新
	@PutMapping("/{username}")
	public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserRequest userRequest, Authentication authentication) {
		return ResponseEntity.ok(userService.update(username, userRequest, authentication));
	}

	// ユーザー削除
	@DeleteMapping("/{username}")
	public ResponseEntity<Void> deleteUser(@PathVariable String username, Authentication authentication) {
		userService.delete(username, authentication);
		return ResponseEntity.noContent().build();
	}
	
	// ログイン処理
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.login(loginRequest));
	}
	
	// ログアウト処理
	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(HttpServletRequest request, Authentication authentication) {
		return ResponseEntity.ok(userService.logout(request, authentication));
	}
}
