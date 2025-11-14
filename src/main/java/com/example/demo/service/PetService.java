package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PetRequest;
import com.example.demo.dto.PetResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

	private final PetRepository petRepository;
	
	// 1件検索処理
	@Transactional(readOnly = true)	// Transactionは基本serviceでの記述を
	public PetResponse findById(int id) { 	//戻り値はpetResponseで
		PetResponse pet = findByIdOrThrow(id);
		
		return pet;
	}
	
	// 全件検索処理
	public List<PetResponse> getPets(String findByStatus, String findByTags) {
		
		List<PetResponse> petResponseList = petRepository.findAll();
		
		List<PetResponse> filtered = filterPet(findByStatus, findByTags, petResponseList);
		
		// フィルタリング時のエラー
		if (filtered.isEmpty() ) {
			throw new ResourceNotFoundException("該当するペットが見つかりませんでした");
		}
		
		return filtered;
		
	}
	
	// 登録処理
	@Transactional
	public PetResponse post(PetRequest petRequest) {
		
		if (petRequest.getPetName() == null || petRequest.getPetName().isBlank()) {
			throw new BadRequestException("名前を入力してください");
		}
		
		if (petRequest.getStatus() == null) {
			throw new BadRequestException("ステータスを入力してください");
		}
		
		petRepository.create(petRequest);
		return petRepository.findById(petRequest.getId());
	}
	// 更新処理
	@Transactional
	public PetResponse put(int id, PetRequest petRequest) {
		findByIdOrThrow(id);
		
		petRequest.setId(id);
		petRepository.update(petRequest);
		
		return petRepository.findById(id);
	}
	
	// 削除処理
	@Transactional
	public void delete(int id) {
		int deleteCount = petRepository.delete(id);
		
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
					.toList();
		}
	
	//404エラーメソッド
		private PetResponse findByIdOrThrow(int id) {
			PetResponse pet = petRepository.findById(id);
			if( pet == null) {
				throw new ResourceNotFoundException("指定されたペットのID " + id + " が見つかりません");
			}
			return pet;
		}
}
