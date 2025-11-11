package com.example.demo.dto;

import lombok.Data;

@Data
public class UserRequest {

	private Integer id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phone;
}
