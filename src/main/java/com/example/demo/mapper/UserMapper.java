package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;

@Mapper
public interface UserMapper {

	UserResponse findByUsername(String username);
	
	int insert(UserRequest userRequest);
	
	int update(UserRequest userRequest);
	
	int delete(String username);
	
}
