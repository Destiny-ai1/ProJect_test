package com.example.demo.category;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import com.example.demo.item.ItemDto;

@Mapper
public interface CategoryDao {
	public List<CategoryDto> findAll();
	
	// 대분류 카테고리
	@Select("select cno,cname from category where pno is null")
	public List<Map> findMajorCategory();
	
	// 하위 분류 카테고리
	@Select("select cno, cname from category where pno=#{cno}")
	public List<Map> findChildCategoryByParent(Long cno);
	
	// 특정 대분류에 해당하는 중간분류와 소분류를 조회
	public List<Map> findCategoryByParentCno(Long cno);
}
