package com.example.demo.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final StringRedisTemplate redisTemplate;
	
	// トークンをブラックリストに登録
	public void addToBlacklist(String token, long expirationMillis) {
		
		redisTemplate.opsForValue().set(
				"blacklist:" + token, "logout", expirationMillis, TimeUnit.MILLISECONDS);
	}
	
	// ブラックリストに登録されているか確認
	public boolean isBlacklisted(String token) {
		
		return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
	}
	
	// リフレッシュトークン保存
	public void saveRefreshToken(String username, String refreshToken, long expirationMs) {
		redisTemplate.opsForValue().set(
			"refresh:" + username,
			refreshToken,
			expirationMs,
			TimeUnit.MILLISECONDS
		);
	}
	
	// リフレッシュトークン取得
	public String getRefreshToken(String username) {
		return redisTemplate.opsForValue().get("refresh:" + username);
	}
	
	// リフレッシュトークン削除
	public void deleteRefreshToken(String username) {
		redisTemplate.delete("refresh:" + username);
	}
}
