package com.example.demo.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserMapper;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

	private final UserMapper userMapper;
	
	public SecurityUserDetailsService(UserMapper userMapper) {
		this.userMapper = userMapper;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userMapper.findByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
		}
		
		boolean enabled = user.getUserStatus() == null ? true : user.getUserStatus() == 1;
		
		return new SecurityUserDetails(user.getUsername(), user.getPassword(), enabled);
	}
}
