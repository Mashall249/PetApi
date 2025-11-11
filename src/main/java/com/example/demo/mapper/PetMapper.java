package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.dto.PetRequest;
import com.example.demo.dto.PetResponse;

@Mapper
public interface PetMapper {

	PetResponse findById(int id);	// Responseの方から呼び出すことで関連テーブルも読み込める
	
	List<PetResponse> findAll();
	
	int insert(PetRequest petRequest);
	
	int update(PetRequest petRequest);
	
	int delete(int id);
}
