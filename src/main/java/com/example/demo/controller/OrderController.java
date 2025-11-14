package com.example.demo.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> findOrderById(@PathVariable int id) {
		return ResponseEntity.ok(orderService.get(id));
	}
	
	@PostMapping
	public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(orderRequest));
		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
		orderService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
