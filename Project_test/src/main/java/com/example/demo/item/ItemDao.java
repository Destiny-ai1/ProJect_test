package com.example.demo.item;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ItemDao {	
	@Insert("INSERT INTO item (item_no, item_irum, item_info, item_price, item_jango, item_sell_qty, add_good_cnt, review_ea, cno) "
            + "VALUES (#{item_no}, #{item_irum}, #{item_info}, #{item_price}, #{item_jango}, #{item_sell_qty}, #{add_good_cnt}, #{review_ea}, #{cno})")
    public Integer save(Item item);
    
    @Select("SELECT item_no, item_irum, item_info, item_price, item_jango, item_sell_qty, add_good_cnt, review_ea, cno FROM item")
    public List<ItemDto.ItemList> findall();
}
