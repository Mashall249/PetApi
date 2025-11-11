package com.example.demo.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.config.SecurityUserDetailsService;
import com.example.demo.redis.RedisService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtUtil jwtUtil;	// トークンの検証用
	
	@Autowired
	private SecurityUserDetailsService userDetailsService;	// ユーザー情報のロード
	
	@Autowired
	private RedisService redisService;	// Redisブラックリスト
	
	@Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/user"); // permitAll に合わせる
	}

	// リクエストごとに1回だけ呼ばれるフィルター
	@Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

		// ヘッダーから"Authorization"を取得
        String authHeader = request.getHeader("Authorization");
        
        // Authorizationヘッダーが存在しない or "Bearer " で始まらない場合はスルー
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	
            // トークンの文字列を取り出す
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            // ブラックリスト確認
            if (redisService.isBlacklisted(token)) {
            	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	return;
            }

            // まだ認証されていない場合
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            	// DBからユーザー情報を取得
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // トークンが有効時に認証情報を登録
                if (jwtUtil.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                            		userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        
        // 次のフィルターもしくはコントローラーへ
        chain.doFilter(request, response);
    }
}
