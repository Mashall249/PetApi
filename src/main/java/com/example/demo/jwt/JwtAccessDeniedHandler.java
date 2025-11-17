package com.example.demo.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.example.demo.advice.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request,
						HttpServletResponse response,
						AccessDeniedException accessDeniedException) throws IOException {

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json;charset=UTF-8");
		
		ApiResponse api = new ApiResponse();
		api.setCode(403);
		api.setType("Forbidden");
		api.setMessage("アクセス権限がありません。");
		
		response.getWriter().write(
				new ObjectMapper().writeValueAsString(api));
		
	}
}
