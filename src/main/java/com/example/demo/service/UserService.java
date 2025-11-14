package com.example.demo.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.redis.RedisService;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;


// Webのアノテーション(@PathVariable, @RequestBody,etc)は含めない
// 基本は素の値を
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
	public UserResponse get(String username, Authentication authentication) {
		validateUserAccess(username, authentication);

		UserResponse user = userRepository.get(username);
		if (user == null) {
			throw new RuntimeException("ユーザーが存在しません。");
		}

		return user;
	}
		
	// 登録処理
	@Transactional
	public UserResponse create(UserRequest userRequest) {

		userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		userRepository.create(userRequest);

		return userRepository.get(userRequest.getUsername());
	}
		
	// ユーザー更新
	@Transactional
	public UserResponse update(String username, UserRequest userRequest, Authentication authentication) {

		validateUserAccess(username, authentication);

		userRequest.setUsername(username);
		userRepository.update(userRequest);

		return userRepository.get(username);
	}

	// ユーザー削除
	@Transactional
	public void delete(String username, Authentication authentication) {

		validateUserAccess(username, authentication);

		userRepository.delete(username);
	}
		
	// ログイン処理
	public String login(LoginRequest loginRequest) {

		Authentication auth = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
		);

		UserDetails userDetails = (UserDetails) auth.getPrincipal();

		// トークンを生成して返却
		return jwtUtil.generateToken(userDetails.getUsername());
	}
		
	// ログアウト処理
	public String logout(HttpServletRequest request, Authentication authentication) {

		String token = extractToken(request);
		String username = authentication.getName();

		long expiration = jwtUtil.getExpirationDate(token).getTime() - System.currentTimeMillis();
		redisService.addToBlacklist(token, expiration);

		return username + "のログアウトが完了しました。トークンは無効化されました。";
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
	private void validateUserAccess(String username, Authentication authentication) {
		// JWTから取得したユーザー名
		String loginUsername = authentication.getName();

		// 自分以外のユーザーがアクセスしていないか
		if(!loginUsername.equals(username)) {
			throw new RuntimeException("他のユーザー情報へのアクセスは許可されていません。");
		}
	}
}
