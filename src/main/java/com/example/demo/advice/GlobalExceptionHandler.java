package com.example.demo.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	// BadRequest(400)
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException ex) {
		ApiResponse response = new ApiResponse();
		response.setCode(HttpStatus.BAD_REQUEST.value());
		response.setType("BadRequest");
		response.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	// NotValid(400)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
	    String message = ex.getBindingResult()
	            .getFieldErrors()
	            .stream()
	            .map(error -> error.getField() + ": " + error.getDefaultMessage())
	            .findFirst()
	            .orElse("不正なリクエストです。");

	    ApiResponse response = new ApiResponse();
	    response.setCode(HttpStatus.BAD_REQUEST.value());
	    response.setType("Validation Error");
	    response.setMessage(message);
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	// NotFound(404)
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ApiResponse> handleBadRequest(ResourceNotFoundException ex) {
		ApiResponse response = new ApiResponse();
		response.setCode(HttpStatus.NOT_FOUND.value());
		response.setType("NotFound");
		response.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	// NotMethodSupport(405)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ResponseEntity<ApiResponse> handleBadRequest(HttpRequestMethodNotSupportedException ex) {
		ApiResponse response = new ApiResponse();
		response.setCode(HttpStatus.METHOD_NOT_ALLOWED.value());
		response.setType("MethodNotAllowed");
		response.setMessage("このエンドポイントではメソッド " + ex.getMethod() + " は許可されていません。");
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
	}
}
