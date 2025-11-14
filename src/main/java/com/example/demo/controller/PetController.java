package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PetRequest;
import com.example.demo.dto.PetResponse;
import com.example.demo.service.PetService;

@RestController
@RequestMapping("/pet")
public class PetController {

	@Autowired
	PetService petService;
	
	// 1件検索
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public PetResponse findById(@PathVariable int id) {
		return petService.findById(id);
	}
	
	//全件検索
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<PetResponse> getPets(
			@RequestParam(required = false) String findByStatus,
			@RequestParam(required = false) String findByTags) {
		
		return petService.getPets(findByStatus, findByTags);
		
	}
	
	//登録
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PetResponse doPost(@RequestBody PetRequest petRequest) {
		return petService.post(petRequest);
	}
	
	//更新
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public PetResponse doPut(@PathVariable int id, @RequestBody PetRequest petRequest) {
		return petService.put(id, petRequest);
	}
	
	//削除
	@DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
	public void doDelete(@PathVariable int id) {
		petService.delete(id);
	}
	
	
}
