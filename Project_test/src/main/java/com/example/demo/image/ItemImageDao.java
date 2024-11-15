package com.example.demo.image;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemImageDao {
	@Insert("insert into item_image values(#{imageNo}, #{itemNo}, #{imageName})")
	public Integer save(ItemImage item_image);
}
