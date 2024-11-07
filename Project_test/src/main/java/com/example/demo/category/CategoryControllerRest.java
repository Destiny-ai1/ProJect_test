package com.example.demo.category;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public class CategoryControllerRest {
	private CategoryService categoryService;
	
	@GetMapping("/api/categories")
	public ResponseEntity<List<Map>> findChildCategoryByParent(Long cno) {
		return ResponseEntity.ok(categoryService.findChildCategoryByParent(cno));
	}
}
