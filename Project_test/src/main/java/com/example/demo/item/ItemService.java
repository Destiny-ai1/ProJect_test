package com.example.demo.item;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.category.CategoryDao;
import com.example.demo.item.ItemDto.ItemList;

public class ItemService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CategoryDao categoryDao;
	
	public List<Map> findMajorCategory() {
		return categoryDao.findMajorCategory();
	}

	public List<ItemList> findAll() {
		return itemDao.findall(/*잠시보류*/);
	}
	
	
}
