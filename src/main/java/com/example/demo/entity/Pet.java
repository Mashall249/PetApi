package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class Pet {

	private int id;
	private int categoryId;
	private int tagId;
	private String name;
	private String photoUrls;
	private Status status = Status.AVAILABLE;
	
	public enum Status {
		AVAILABLE("available"),
		PENDING("pending"),
		SOLD("sold");
		
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
