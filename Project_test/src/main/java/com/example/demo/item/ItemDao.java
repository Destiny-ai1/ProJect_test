package com.example.demo.item;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.demo.image.ItemImage;
import com.example.demo.item.ItemDto.ItemList;

@Mapper
public interface ItemDao {
    // item 목록 insert
    public Integer save(Item item);

    // 아이템을 리스트로 선택
    @Select("SELECT item_no, item_irum, item_info, item_price, item_jango, item_sell_qty, add_good_cnt, review_ea, cno, "
            + "   #{imageUrl} || COALESCE((SELECT ii.image_name FROM item_image ii WHERE it.item_no = ii.item_no AND ROWNUM = 1), 'normal/default-image.jpg') AS item_image "
            + "FROM item it")
    public List<ItemDto.ItemList> findAll(@Param("imageUrl") String imageUrl);

    // 상품 번호로 찾기
    public ItemDto.Read findById(Long itemNo, String imageUrl);
    
    // 상품에 해당하는 가격 찾기
    @Select("select item_price from item where item_no=#{item_no} and rownum=1")
    public Integer findPriceByPno(Long itemNo);
    
    // 잔고가 1개 이상인 상품은 주문 가능
    @Select("select case when item_jango>#{count} then 1 else 0 end from item where item_no=#{item_no} and rownum=1")
    public Boolean availabelToOrder(Long itemNo, Integer count);
    
    // 상품 번호가 일치하는 상품의 이미지정보 삭제
    @Delete("delete from item_image where item_no = #{itemNo}")
    public Integer deleteItemImageByItemNo(Long itemNo);
    
    // 상품 번호가 일치하는 상품의 정보 삭제
    @Delete("delete from item where item_no = #{itemNo}")
    public Integer deleteItemByItemNo(Long itemNo);
    
    // itemNo로 이미지 목록을 조회
    @Select("select * from item_image where item_no = #{itemNo}")
    public List<ItemImage> findByItemNo(Long itemNo);
    
    // itemNo에 해당하는 모든 이미지를 삭제
    @Delete("delet from item_image where item_no = #{itemNo}")
    public void deleteByItemNo(Long itemNo);

    // 카테고리 번호에 해당하는 상품들을 조회하는 메서드
	public List<ItemList> findItemsByCategory(Long cno, String imageUrl);
    
	// 상품별 평균 평점 조회
    public Double findAverageRatingByItemNo(Long itemNo);
}
