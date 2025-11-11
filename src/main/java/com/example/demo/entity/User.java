package com.example.demo.entity;

import lombok.Data;

@Data
public class User {

	private Integer id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phone;
	private Integer userStatus = 1;
	
}
