package com.example.demo.controller;

import jakarta.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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
import com.example.demo.dto.PetResponse;
import com.example.demo.entity.Order;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.mapper.PetMapper;

@RestController
@RequestMapping("/store/order")
public class OrderController {

	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	PetMapper petMapper;
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public OrderResponse findById(@PathVariable int id) {
		Order order = orderMapper.findById(id);
		
		if( order == null) {
			throw new ResourceNotFoundException("指定されたオーダーのID " + id + " が見つかりません");
		}
		
		return responseOrder(order);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrderResponse doPost(@Valid @RequestBody OrderRequest orderRequest) {
		 PetResponse pet = petMapper.findById(orderRequest.getPetId());
		    if (pet == null) {
		        throw new BadRequestException("指定されたpetId " + orderRequest.getPetId() + " は存在しません。");
		    }
		
		Order order = new Order();
		
		BeanUtils.copyProperties(orderRequest, order);
		orderMapper.insert(order);
		
		return responseOrder(order);
		
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public void delete(@PathVariable int id) {
		int deleteCount = orderMapper.delete(id);
		
		if(deleteCount == 0) {
			throw new ResourceNotFoundException("指定されたオーダーのID " + id + " が見つかりません");
		}
	}
	
	// レスポンスにデータをコピーするメソッド
	private OrderResponse responseOrder(Order order) {
		OrderResponse orderResponse = new OrderResponse();
		BeanUtils.copyProperties(order, orderResponse);
		return orderResponse;
	}
}
