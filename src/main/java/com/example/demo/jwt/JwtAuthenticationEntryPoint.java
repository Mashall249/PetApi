package com.example.demo.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.example.demo.advice.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
						HttpServletResponse response,
						AuthenticationException authException) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		
		ApiResponse api = new ApiResponse();
		api.setCode(401);
		api.setType("Unauthorized");
		api.setMessage("認証が必要です。");
		
		response.getWriter().write(
				new ObjectMapper().writeValueAsString(api));
		
	}
}
