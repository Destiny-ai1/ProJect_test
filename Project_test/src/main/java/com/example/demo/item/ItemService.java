package com.example.demo.item;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.category.CategoryDao;
import com.example.demo.item.ItemDto.ItemList;

import jakarta.validation.Valid;

@Service
public class ItemService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CategoryDao categoryDao;
	
	// 카테고리 대분류
	public List<Map> findMajorCategory() {
		return categoryDao.findMajorCategory();
	}
	
	// 24.11.07 미완성
	public List<ItemList> findAll() {
		return itemDao.findall(/*잠시보류*/);
	}
	
	// 24.11.07 미완성
	public void save(@Valid ItemDto.Create dto) {
		return;
	}
	
	
	
	
}
