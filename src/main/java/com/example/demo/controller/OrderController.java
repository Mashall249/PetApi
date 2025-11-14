package com.example.demo.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/store/order")
public class OrderController {

	@Autowired
	OrderService orderService;
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public OrderResponse findById(@PathVariable int id) {
		return orderService.get(id);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponse doPost(@Valid @RequestBody OrderRequest orderRequest) {
		return orderService.post(orderRequest);
		
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable int id) {
		orderService.delete(id);
	}
}
