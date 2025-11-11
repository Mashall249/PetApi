package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class Order {

	private int id;
	private int petId;
	private int quantity;
	private String shipDate;
	private Status status = Status.PLACED;
	private boolean complete = false;
	
	public enum Status {
		PLACED("placed"),
		APPROVED("approved"),
		DELIVERED("delivered");
		
		private final String label;
		
		Status(String label) {
			this.label = label;
		}
		
		@JsonValue		// ()内の表記にする
		public String getLabel() {
			return label;
		}
	}
}
