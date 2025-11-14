package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PetRequest;
import com.example.demo.dto.PetResponse;
import com.example.demo.service.PetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

	private final PetService petService;
	
	// 1件検索
	@GetMapping("/{id}")
	public ResponseEntity<PetResponse> findPetById(@PathVariable int id) {
		return ResponseEntity.ok(petService.findById(id));
	}
	
	//全件検索
	@GetMapping
	public ResponseEntity<List<PetResponse>> findPetsAll(
			@RequestParam(required = false) String findByStatus,
			@RequestParam(required = false) String findByTags) {
		
		return ResponseEntity.ok(petService.getPets(findByStatus, findByTags));
		
	}
	
	//登録
	@PostMapping
	public ResponseEntity<?> createPet(@RequestBody PetRequest petRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(petService.post(petRequest));
	}
	
	//更新
	@PutMapping("/{id}")
	public ResponseEntity<?> updatePet(@PathVariable int id, @RequestBody PetRequest petRequest) {
		return ResponseEntity.ok(petService.put(id, petRequest));
	}
	
	//削除
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePet(@PathVariable int id) {
		petService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
