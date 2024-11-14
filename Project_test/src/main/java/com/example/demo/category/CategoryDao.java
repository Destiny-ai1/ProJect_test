package com.example.demo.category;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryDao {
	public List<CategoryDto> findAll();
	
	@Select("select cno,cname from category where pno is null")
	public List<Map> findMajorCategory();
	
	@Select("select cno, cname from category where pno=#{cno}")
	public List<Map> findChildCategoryByParent(Long cno);
}
