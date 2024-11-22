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
	
	public List<Map> findChildCategoryByParent(Long cno) {
		return categoryDao.findChildCategoryByParent(cno);
	}

	public List<Map> findCategoryByParentCno(Long cno) {
		return categoryDao.findCategoryByParentCno(cno);
	}
}
