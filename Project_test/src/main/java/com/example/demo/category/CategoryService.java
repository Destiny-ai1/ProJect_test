package com.example.demo.category;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.item.ItemDto;
import com.example.demo.item.ItemDto.ItemList;
import com.example.demo.item.ItemService;

@Service
public class CategoryService {
	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private ItemService itemService;
	
	// 대분류 카테고리 조회
    public List<Map> findMajorCategory() {
        return categoryDao.findMajorCategory();
    }
	
	// 부모 cno를 이용하여 자식 카테고리 검색
	public List<Map> findChildCategoryByParent(Long cno) {
		return categoryDao.findChildCategoryByParent(cno);
	}
	
	// 부모 cno를 이용하여 카테고리 검색
	public List<Map> findCategoryByParentCno(Long cno) {
		return categoryDao.findCategoryByParentCno(cno);
	}
	
	 /**
     * 특정 카테고리에서 제외할 카테고리 번호를 받아서
     * 해당 카테고리 및 하위 카테고리의 상품을 조회한 후 제외된 카테고리 상품을 제외한 결과를 반환.
     */
	public List<ItemDto.ItemList> findItemsByCategoryExcluding(Long categoryCno, Long excludeCno) {
	    // 1. 해당 카테고리와 하위 카테고리의 상품들을 조회
	    List<ItemDto.ItemList> allItems = itemService.findItemsByCategory(categoryCno, null, excludeCno); // imageUrl은 필요 없으면 null로 설정

	    // 2. 제외할 카테고리(excludeCno) 번호에 해당하는 상품을 제외
	    List<ItemDto.ItemList> filteredItems = allItems.stream()
	        .filter(item -> !item.getCno().equals(excludeCno))  // getCno()를 사용하여 필터링
	        .collect(Collectors.toList());

	    return filteredItems; // 필터링된 상품 목록 반환
	}

}
