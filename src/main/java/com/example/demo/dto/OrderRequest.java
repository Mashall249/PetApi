package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.example.demo.entity.Order.Status;

import lombok.Data;

@Data
public class OrderRequest {

	@NotNull(message = "petのidは必須です")
	private Integer petId;
	
	@NotNull(message = "個体数は必須です")
	@Min(value = 1, message = "登録は1匹から")
	private Integer quantity;
	private String shipDate;
	private Status status;
	private boolean complete ;
}
