package com.example.demo.item;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ItemDao {	
	// item 목록 insert
    public Integer save(Item item);
    
    @Select("SELECT item_no, item_irum, item_info, item_price, item_jango, item_sell_qty, add_good_cnt, review_ea, cno, #{imageUrl} || (select name from product_image pi WHERE p.item_no = pi.item_no and pi.ino = 0) as image from item")
    public List<ItemDto.ItemList> findAll(String imageUrl);
    
    public ItemDto.Read findById(Integer item_no, String imageUrl);
    
    @Select("select price from item where item_no=#{item_no} and rownum=1")
    public Integer findPriceByPno(Long item_no);
    
    @Select("select case when item_jango>#{count} then 1 else 0 end from item where item_no=#{item_no} and rownum=1")
    public Boolean availabelToOrder(Long item_no, Integer count);
}
