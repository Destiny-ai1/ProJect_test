package com.example.demo.category;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public class CategoryService {
	@Autowired
	private CategoryDao categoryDao;
	
	public List<Map> findChildCategoryByParent(Long cno) {
		return categoryDao.findChildCategoryByParent(cno);
	}
}