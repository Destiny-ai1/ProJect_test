package com.example.demo.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ItemRestController {

	@Autowired
	private ItemService itemService;

	// 동적으로 상품 번호를 받아서 해당 상품 삭제
	@DeleteMapping("/item/delete/{itemNo}")
	public String deleteItem(@PathVariable Long itemNo) {
		boolean isDeleted = itemService.deleteItem(itemNo);

		if (isDeleted) {
			return "success";
		} else {
			return "fail";
		}
	}

	// 상품 가격 업데이트 API
	@PatchMapping("/item/{itemNo}/update-price")
	public ResponseEntity<String> updatePrice(@PathVariable Long itemNo, @RequestBody ItemDto.price_update dto) {
		String username = "winter_shop"; // 하드코딩된 username

		dto.setItemNo(itemNo);

		boolean isUpdated = itemService.updatePrice(dto, username);

		if (isUpdated) {
			return ResponseEntity.ok("success");
		} else {
			return ResponseEntity.badRequest().body("fail");
		}
	}

	// 상품 사이즈 목록 조회 API
	@GetMapping("/item/{itemNo}/sizes")
	public ResponseEntity<List<String>> getItemSizes(@PathVariable Long itemNo) {
		// 해당 상품의 사이즈 목록을 조회
		List<String> sizes = itemService.getItemSizes(itemNo);

		if (sizes.isEmpty()) {
			return ResponseEntity.notFound().build(); // 상품 사이즈가 없을 경우
		}

		return ResponseEntity.ok(sizes); // 사이즈 목록을 반환
	}

	// 상품 사이즈별 재고 조회 API
	@PostMapping("/get-stock")
	public ResponseEntity<Map<String, Long>> getItemStock(@RequestParam Long itemNo, @RequestParam String itemSize) {
		Long stock = itemService.getItemStock(itemNo, itemSize);

		if (stock == null) {
			return ResponseEntity.notFound().build();
		}

		// Map을 이용해 JSON 형태로 응답
		Map<String, Long> response = new HashMap<>();
		response.put("currentStock", stock);

		return ResponseEntity.ok(response); // JSON 응답
	}

	// 재고 업데이트 API (POST 방식)
	@PostMapping("/item/{itemNo}/update-stock") // itemNo를 경로에 포함
	public ResponseEntity<String> updateStock(@PathVariable Long itemNo,
			@RequestBody ItemDto.jango_update jangoUpdateDto) {
		String username = "winter_shop"; // 하드코딩된 username

		// itemNo를 DTO에 설정
		jangoUpdateDto.setItemNo(itemNo);

		boolean isUpdated = itemService.updateJango(jangoUpdateDto, username);

		if (isUpdated) {
			return ResponseEntity.ok("재고가 성공적으로 변경되었습니다.");
		} else {
			return ResponseEntity.badRequest().body("재고 변경에 실패했습니다.");
		}
	}
}
