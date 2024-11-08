package com.example.demo.image;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageDao {
	@Insert("insert into item_image values(#{pno}, #{ino}, #{name})")
	public Integer save(ItemImage image);
}
