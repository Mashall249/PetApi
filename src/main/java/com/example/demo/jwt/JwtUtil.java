package com.example.demo.jwt;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	// トークンの秘密鍵
	private final String SECRET_KEY = "secret-key-secret-key-secret-key-secret-key-secret-key";
	
	// トークンの有効期間(1時間)
	private final long EXPIRATION_TIME = 1000 * 60 * 60;
	
	// 秘密鍵をキーオブジェクトに変換
	private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	
	// トークン生成
	public String generateToken(String username) {
		return Jwts.builder()
				.setSubject(username)	// トークンの主体
				.setIssuedAt(new Date())	// 発行日時
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))	// 有効期限
				.signWith(key, SignatureAlgorithm.HS256)	// 署名
				.compact();		// トークンを文字列化
	}
	
	// トークンからユーザー名を抽出
	public String extractUsername(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)		// 検証に使用する鍵
				.build()
				.parseClaimsJws(token)	// トークン解析
				.getBody()
				.getSubject();	// トークンの主体を取得
	}
	
	// クレームを抽出
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	    final Claims claims = Jwts.parserBuilder()
	            .setSigningKey(key)
	            .build()
	            .parseClaimsJws(token)
	            .getBody();
	    return claimsResolver.apply(claims);
	}
	
	// トークンの有効性を検証
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	// トークンの有効期限を取得
	public Date getExpirationDate(String token) {

		return extractClaim(token, Claims::getExpiration);
	}
	
}
