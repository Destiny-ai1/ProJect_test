package com.example.demo.category;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CategoryControllerRest {
	@Autowired
	private CategoryService categoryService;
	
	// 상품 추가시 카테고리 분류
	@GetMapping("/categories")
	public ResponseEntity<List<Map>> findChildCategoryByParent(@RequestParam Long cno) {
		return ResponseEntity.ok(categoryService.findChildCategoryByParent(cno));
	}	
}
