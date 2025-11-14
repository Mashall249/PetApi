package com.example.demo.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.PetResponse;
import com.example.demo.entity.Order;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final PetRepository petRepository;
	
	@Transactional(readOnly = true)
	public OrderResponse get(int id) {
		Order order = orderRepository.get(id);
		
		if( order == null) {
			throw new ResourceNotFoundException("指定されたオーダーのID " + id + " が見つかりません");
		}
		
		return responseOrder(order);
	}
	
	@Transactional
	public OrderResponse create(OrderRequest orderRequest) {
		PetResponse pet = petRepository.findById(orderRequest.getPetId());
		    if (pet == null) {
		        throw new BadRequestException("指定されたpetId " + orderRequest.getPetId() + " は存在しません。");
		    }
		
		Order order = new Order();
		
		BeanUtils.copyProperties(orderRequest, order);
		orderRepository.create(order);
		
		return responseOrder(order);
		
	}
	
	@Transactional
	public void delete(int id) {
		int deleteCount = orderRepository.delete(id);
		
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
