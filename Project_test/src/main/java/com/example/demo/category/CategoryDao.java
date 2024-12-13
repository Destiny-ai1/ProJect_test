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
	
	// 특정 카테고리에서 제외할 상품을 제외한 상품 목록 조회
    @Select("SELECT * FROM items WHERE category_cno = #{categoryCno} AND cno != #{excludeCno}")
    public List<Map> findItemsByCategoryExcluding(Long categoryCno, Long excludeCno);
    
    //  부모 cno가 2인 상품을 먼저 불러오고, 제외할 cno를 제외하는 쿼리
    @Select("SELECT * FROM items WHERE category_cno IN (SELECT cno FROM category WHERE pno = 2) AND cno != #{excludeCno}")
    public List<Map> findItemsByParentCategoryExcluding(Long excludeCno);
}
