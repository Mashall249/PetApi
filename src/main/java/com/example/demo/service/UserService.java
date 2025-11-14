package com.example.demo.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.redis.RedisService;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository ;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final RedisService redisService;
	
	// ユーザー参照処理
	@Transactional(readOnly = true)
	public ResponseEntity<?> get(@PathVariable String username, Authentication authentication) {
		
	ResponseEntity<?> accessError = validateUserAccess(username, authentication);
		if (accessError != null) return accessError;

		UserResponse user = userRepository.get(username);
		if (user == null) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("ユーザーが存在しません。");
		}

		return ResponseEntity.ok(user);
	}
		
	// 登録処理
	@Transactional
	public UserResponse post(@RequestBody UserRequest userRequest) {

		userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		userRepository.create(userRequest);

		return userRepository.get(userRequest.getUsername());
	}
		
	// ユーザー更新
	@Transactional
	public ResponseEntity<?> update(@PathVariable String username, @RequestBody UserRequest userRequest, Authentication authentication) {

		ResponseEntity<?> accessError = validateUserAccess(username, authentication);
		if (accessError != null) return accessError;

		userRequest.setUsername(username);
		userRepository.update(userRequest);

		return ResponseEntity.ok(userRepository.get(username));
	}

	// ユーザー削除
	@Transactional
	public ResponseEntity<?> delete(@PathVariable String username, Authentication authentication) {

		ResponseEntity<?> accessError = validateUserAccess(username, authentication);
		if (accessError != null) return accessError;

		userRepository.delete(username);
		return ResponseEntity.ok(username + "のアカウントを削除しました。");
	}
		
	// ログイン処理
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
