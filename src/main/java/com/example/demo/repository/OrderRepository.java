package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;
import com.example.demo.mapper.OrderMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

	private final OrderMapper orderMapper;
	
	public Order get(int id) {
		return orderMapper.findById(id);
	}
	
	public int create(Order order) {
		return orderMapper.insert(order);
	}
	
	public int delete(int id) {
		return orderMapper.delete(id);
	}
}
