package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PetMapper;

@RestController
@RequestMapping("/pet")
public class PetController {

	@Autowired
	PetMapper petMapper;
	
	// 1件検索
	@GetMapping("/{id}")	//戻り値はpetResponseで
	@ResponseStatus(HttpStatus.OK)
	public PetResponse findById(@PathVariable int id) {
		PetResponse pet = petNotFound(id);
		
		return pet;
	}
	
	//全件検索
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<PetResponse> getPets(
			@RequestParam(required = false) String findByStatus,
			@RequestParam(required = false) String findByTags) {
		
		List<PetResponse> petResponseList = petMapper.findAll();
		
		List<PetResponse> filtered = filterPet(findByStatus, findByTags, petResponseList);
		
		// フィルタリング時のエラー
		if (filtered.isEmpty() ) {
			throw new ResourceNotFoundException("該当するペットが見つかりませんでした");
		}
		
		return filtered;
		
	}
	
	//登録
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional
	public PetResponse doPost(@RequestBody PetRequest petRequest) {
		
		if (petRequest.getPetName() == null || petRequest.getPetName().isBlank()) {
			throw new BadRequestException("名前を入力してください");
		}
		
		if (petRequest.getStatus() == null) {
			throw new BadRequestException("ステータスを入力してください");
		}
		
		petMapper.insert(petRequest);
		return petMapper.findById(petRequest.getId());
	}
	
	//更新
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Transactional
	public PetResponse doPut(@PathVariable int id, @RequestBody PetRequest petRequest) {
		petNotFound(id);
		
		petRequest.setId(id);
		petMapper.update(petRequest);
		
		return petMapper.findById(id);
	}
	
	//削除
	@DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public void doDelete(@PathVariable int id) {
		int deleteCount = petMapper.delete(id);
		
		if(deleteCount == 0) {
			throw new ResourceNotFoundException("指定されたペットのID " + id + " が見つかりません");
		}
	}
	
	//フィルタリング
	private List<PetResponse> filterPet(String findByStatus, String findByTags, List<PetResponse> petResponseList) {

		return petResponseList.stream()
				.filter(pet -> findByStatus == null
	                    || pet.getStatus().name().equalsIgnoreCase(findByStatus)
	                    || pet.getStatus().getLabel().equals(findByStatus))
				.filter(pet -> findByTags == null
						|| pet.getTagName().contains(findByTags))
				.collect(Collectors.toList());
	}
	
	//404エラーメソッド
	private PetResponse petNotFound(int id) {
		PetResponse pet = petMapper.findById(id);
		if( pet == null) {
			throw new ResourceNotFoundException("指定されたペットのID " + id + " が見つかりません");
		}
		return pet;
	}
}
