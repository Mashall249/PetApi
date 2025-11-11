package com.example.demo.dto;

import com.example.demo.entity.Order.Status;

import lombok.Data;

@Data
public class OrderResponse {

	private int id;
	private int petId;
	private int quantity;
	private String shipDate;
	private Status status;
	private boolean complete ;
}
