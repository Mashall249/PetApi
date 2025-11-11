package com.example.demo.dto;

import com.example.demo.entity.Pet.Status;

import lombok.Data;

@Data	//　Mapper.xml内SQLのselectと同じように揃える
public class PetResponse {
	
	private int id;
	private String categoryName;
	private String petName;
	private String photoUrls;
	private String tagName;
	private Status status;

}
