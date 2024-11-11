package com.example.demo.image;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ImageDao {
	@Insert("insert into item_image values(#{pno}, #{ino}, #{name})")
	public Integer save(ItemImage image);
	
	@Select("select item_no from item_image where item_no=#{item_no}")
	public String findImageUrlByItemId(Long itemNo);
}
