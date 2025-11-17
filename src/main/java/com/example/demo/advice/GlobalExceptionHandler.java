package com.example.demo.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	// BadRequest(400)
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "BadRequest", ex.getMessage());
	}
	
	// ValidationError(400)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
	    String message = ex.getBindingResult()
	            .getFieldErrors()
	            .stream()
	            .map(error -> error.getField() + ": " + error.getDefaultMessage())
	            .findFirst()
	            .orElse("不正なリクエストです。");

	    return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", message);
	}
	
	// IllegalArgument(400)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "InvalidArgument", ex.getMessage());
		
	}
	// Unauthorized(401)
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse> handleUnauthorized(UnauthorizedException ex) {
		return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
	}
	
	// Forbidden(403)
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ApiResponse> handleForbidden(ForbiddenException ex) {
		return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
	}
	
	// NotFound(404)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException ex) {
		return buildResponse(HttpStatus.NOT_FOUND, "NotFound", ex.getMessage());
	}
	
	// NotMethodSupport(405)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
		String message = "このエンドポイントではメソッド " + ex.getMethod() + " は許可されていません。";
		
		return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "MethodNotAllowed", message);
	}
	
	// Exception(500)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleGeneralError(Exception ex) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ServerError", "サーバー内部でエラーが発生しました。");
	}
	
	// 共通レスポンス生成
	private ResponseEntity<ApiResponse> buildResponse(HttpStatus status, String type, String message) {
        ApiResponse response = new ApiResponse();
        response.setCode(status.value());
        response.setType(type);
        response.setMessage(message);

        return ResponseEntity.status(status).body(response);
    }
}
