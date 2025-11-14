package com.example.demo.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.dto.PetRequest;
import com.example.demo.dto.PetResponse;
import com.example.demo.mapper.PetMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PetRepository {

	private final PetMapper petMapper;
	
	public PetResponse findById(int id) {
		return petMapper.findById(id);
	}
	
	public List<PetResponse> findAll() {
		return petMapper.findAll();
	}
	
	public int create(PetRequest petRequest) {
		return petMapper.insert(petRequest);
	}
	
	public int update(PetRequest petRequest) {
		return petMapper.update(petRequest);
		}
	
	public int delete(int id) {
		return petMapper.delete(id);
	}
	
}
