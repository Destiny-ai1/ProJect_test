package com.example.demo.item;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.category.CategoryDao;
import com.example.demo.image.ImageDao;
import com.example.demo.item.ItemDto.ItemList;

import jakarta.validation.Valid;

@Service
public class ItemService {
	@Value("${itemimage.url}")
	private String imgeUrl;
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private ImageDao imageDao;
	
	// 카테고리 대분류
	public List<Map> findMajorCategory() {
		return categoryDao.findMajorCategory();
	}
	
	// 생성자 주입
	public ItemService(ItemDao itemDao) {
		this.itemDao = itemDao;
	}
	
	public List<ItemDto.ItemList> findAll() {
		return itemDao.findAll(imgeUrl);
	}
	
	// 24.11.07 미완성
	public void save(@Valid ItemDto.Create dto) {
		return;
	}

	public static Item findItemById(Long item_no) {
		return null;
	}
	
	
	
	
}
