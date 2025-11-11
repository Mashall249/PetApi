package com.example.demo.mapper;

import com.example.demo.entity.Order;

public interface OrderMapper {
	
	Order findById(int id);
	
	int insert(Order order);
	
	int delete(int id);

}
