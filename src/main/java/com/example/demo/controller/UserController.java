package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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
import com.example.demo.jwt.JwtUtil;
import com.example.demo.mapper.UserMapper;
import com.example.demo.redis.RedisService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RedisService redisService;
	
	@GetMapping("/{username}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> findByUsername(@PathVariable String username, Authentication authentication) {
		
		ResponseEntity<?> accessError = validateUserAccess(username, authentication);
        if (accessError != null) return accessError;
		
		UserResponse user = userMapper.findByUsername(username);
		if (user == null) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("ユーザーが存在しません。");
		}
		
		return ResponseEntity.ok(user);
	}
	
	// 登録
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public UserResponse doPost(@RequestBody UserRequest userRequest) {
		
		userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		userMapper.insert(userRequest);
		
		return userMapper.findByUsername(userRequest.getUsername());
	}
	
	@PutMapping("/{username}")
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserRequest userRequest, Authentication authentication) {

		ResponseEntity<?> accessError = validateUserAccess(username, authentication);
        if (accessError != null) return accessError;

		userRequest.setUsername(username);
		userMapper.update(userRequest);

		return ResponseEntity.ok(userMapper.findByUsername(username));
	}

	
	@DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public ResponseEntity<?> deleteUser(@PathVariable String username, Authentication authentication) {

		ResponseEntity<?> accessError = validateUserAccess(username, authentication);
        if (accessError != null) return accessError;

		userMapper.delete(username);
		return ResponseEntity.ok(username + "のアカウントを削除しました。");
	}
	
	// ログイン処理
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		
		Authentication auth = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
		);
		
		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		
		// トークンを生成して返却
		String token = jwtUtil.generateToken(userDetails.getUsername());
		
		return ResponseEntity.ok("ログイン成功。トークン: " + token);
	}
	
	// ログアウト処理
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, Authentication authentication) {
		try {
			String token = extractToken(request);
			String username = authentication.getName();

			long expiration = jwtUtil.getExpirationDate(token).getTime() - System.currentTimeMillis();
			redisService.addToBlacklist(token, expiration);

			return ResponseEntity.ok(username + "のログアウトが完了しました。トークンは無効化されました。");

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	// Bearerトークン抽出メソッド
	private String extractToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
				
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
					
			throw new IllegalArgumentException("トークンが存在しません。");
		}
		return authHeader.substring(7);
	}
	
	// ユーザーチェック
	private ResponseEntity<String> validateUserAccess(String username, Authentication authentication) {
		// JWTから取得したユーザー名
		String loginUsername = authentication.getName();
		
		// 自分以外のユーザーがアクセスしていないか
		if(!loginUsername.equals(username)) {
			
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("他のユーザー情報へのアクセスは許可されていません。");
		}
		return null;
	}

}
