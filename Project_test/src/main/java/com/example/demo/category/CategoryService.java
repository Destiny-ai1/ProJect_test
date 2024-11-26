package com.example.demo.category;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.item.ItemDto;
import com.example.demo.item.ItemService;

@Service
public class CategoryService {
	@Autowired
	private CategoryDao categoryDao;
	
	// 부모 cno를 이용하여 자식 카테고리 검색
	public List<Map> findChildCategoryByParent(Long cno) {
		return categoryDao.findChildCategoryByParent(cno);
	}
	
	// 부모 cno를 이용하여 카테고리 검색
	public List<Map> findCategoryByParentCno(Long cno) {
		return categoryDao.findCategoryByParentCno(cno);
	}
}
