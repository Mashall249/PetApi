package com.example.demo.service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.exception.UnauthorizedException;
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
	@Transactional
	public LoginResponse login(LoginRequest loginRequest) {

		// ユーザー認証
		Authentication auth = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
		);

		UserDetails userDetails = (UserDetails) auth.getPrincipal();
		String username = userDetails.getUsername();

		// アクセストークン・リフレッシュトークン生成
		String accessToken = jwtUtil.generateAccessToken(username);
		String refreshToken = jwtUtil.generateRefreshToken(username);
		
		// リフレッシュトークンをRedisに保存
		redisService.saveRefreshToken(username, refreshToken, jwtUtil.getExpirationMs());
		
		return new LoginResponse(accessToken, refreshToken, username);
	}
		
	// ログアウト処理
	@Transactional
	public String logout(HttpServletRequest request, Authentication authentication) {

		String accessToken = extractBearerToken(request);
		String username = (authentication != null) ? authentication.getName() : "Unknown User";
		String refreshToken = jwtUtil.extractRefreshTokenFromCookie(request);

		// リフレッシュトークンをRedisから削除
		if (refreshToken != null) {
			redisService.deleteRefreshToken(username);
		}

		// アクセストークンのブラックリスト化
		long expiration = jwtUtil.getExpirationDate(accessToken).getTime() - System.currentTimeMillis();
		redisService.addToBlacklist(accessToken, expiration);

		return username + "のログアウトが完了しました。トークンは無効化されました。";
	}

	// リフレッシュ処理
	@Transactional
	public String rotateRefreshToken(HttpServletRequest request) {

		// Cookieからリフレッシュトークンを取得
		String oldRefreshToken = jwtUtil.extractRefreshTokenFromCookie(request);
		if (oldRefreshToken == null) {
			throw new UnauthorizedException("リフレッシュトークンがありません。");
		}
		
		// リフレッシュトークンの有効性チェック
		String username = jwtUtil.extractUsername(oldRefreshToken);
		
		// Redis二保存されているリフレッシュトークンと一致するか
		String savedToken = redisService.getRefreshToken(username);
		if(!oldRefreshToken.equals(savedToken)) {
			throw new UnauthorizedException("リフレッシュトークンが無効です（ローテーション済みの可能性あり）。");
		}
		
		// 古いリフレッシュトークンを削除
		redisService.deleteRefreshToken(username);
		
		// 新しいリフレッシュトークンを取得
		String newRefreshToken = jwtUtil.generateRefreshToken(username);
		
		// Redisに保存
		redisService.saveRefreshToken(username, newRefreshToken, jwtUtil.getExpirationMs());
		
		// 新しいアクセストークンを生成
		String newAccessToken = jwtUtil.generateAccessToken(username);
		
		// リフレッシュトークンを再Cookie化
		ResponseCookie remakeCookie = ResponseCookie.from("refreshToken", newRefreshToken)
				.httpOnly(true)
				.secure(false)
				.sameSite("None")
				.path("/")
				.maxAge(jwtUtil.getExpirationMs() / 1000)
				.build();
		
		request.setAttribute("Set-Cookie", remakeCookie.toString());
		
		return newAccessToken;
	}
		
	// Bearerトークンを抽出
	private String extractBearerToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {

			throw new UnauthorizedException("トークンが存在しません。");
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
