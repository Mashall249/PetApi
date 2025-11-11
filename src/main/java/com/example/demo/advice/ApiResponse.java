package com.example.demo.advice;

import lombok.Data;

@Data
public class ApiResponse {

	private Integer code;
	private String message;
	private String type;
}
