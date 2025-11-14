package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {
	
	private final UserMapper userMapper;
	
	public UserResponse get(String username) {
		return userMapper.findByUsername(username);
	}
	
	public int create(UserRequest userRequest) {
		return userMapper.insert(userRequest);
	}
	
	public int update(UserRequest userRequest) {
		return userMapper.update(userRequest);
	}
	
	public int delete(String username) {
		return userMapper.delete(username);
	}

}
