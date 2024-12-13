package com.example.demo.item;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.image.ItemImage;
import com.example.demo.item.ItemDto.ItemList;

@Mapper
public interface ItemDao {

    // item 목록 insert
    public Integer save(Item item);

    // 상품 사이즈 저장 (item_size 테이블에 삽입)
    public Integer saveItemSize(@Param("itemNo") Long itemNo, @Param("itemSize") String itemSize, @Param("itemJango") Integer itemJango);

    // 아이템을 리스트로 선택
    @Select("SELECT it.item_no, it.item_irum, it.item_info, it.item_price, it.item_jango, it.item_sell_qty, "
            + "it.review_ea, it.cno, "
            + "#{imageUrl} || COALESCE((SELECT ii.image_name "
            + "FROM item_image ii "
            + "WHERE it.item_no = ii.item_no AND ROWNUM = 1), 'normal/default-image.jpg') AS item_image "
            + "FROM item it "
            + "ORDER BY it.item_no ASC")
    public List<ItemDto.ItemList> findAll(@Param("imageUrl") String imageUrl);

    // 상품 번호로 상품 찾기
    public ItemDto.Read findById(Long itemNo, String imageUrl);
    
    // 상품에 해당하는 가격 찾기
    @Select("select item_price from item where item_no=#{itemNo} and rownum=1")
    public Integer findPriceByItemNo(Long itemNo);
    
    // 재고가 1개 이상인 상품은 주문 가능
    @Select("select case when item_jango>#{cartEa} then 1 else 0 end from item where item_no=#{itemNo} and rownum=1")
    public Boolean availableToOrder(Long itemNo, Integer cart_ea);
    
    // 상품 번호가 장바구니에 존재하는 모든 항목 삭제 (모든 사용자)
    @Delete("DELETE FROM cart WHERE item_no = #{itemNo}")
    public void deleteFromCartByItemNo(Long itemNo);
    
    // 상품 번호가 일치하는 상품의 사이즈 정보 삭제
    @Delete("DELETE FROM item_size WHERE item_no = #{itemNo}")
    public Integer deleteItemSizeByItemNo(Long itemNo);
    
    // 상품 번호가 일치하는 상품의 이미지정보 삭제
    @Delete("delete from item_image where item_no = #{itemNo}")
    public Integer deleteItemImageByItemNo(Long itemNo);
    
    // 상품 번호가 일치하는 상품의 정보 삭제
    @Delete("delete from item where item_no = #{itemNo}")
    public Integer deleteItemByItemNo(Long itemNo);
    
    // itemNo로 이미지 목록을 조회
    @Select("SELECT * FROM item_image WHERE item_no = #{itemNo}")
    public List<ItemImage> findByItemNo(Long itemNo); // 이 부분을 추가

    // 카테고리 번호에 해당하는 상품들을 조회하는 메서드
    public List<ItemList> findItemsByCategory(Long cno, String imageUrl);
    
    // 상품별 평균 평점 조회
    public Double findAverageRatingByItemNo(Long itemNo);
    
    // 재고 수량 조회 (상품 번호로 조회)
    @Select("SELECT item_jango FROM item_size WHERE item_no = #{itemNo} AND item_size = #{itemSize}")
    Integer getStockByItemSize(@Param("itemNo") Long itemNo, @Param("itemSize") String itemSize);
    
    // 상품의 가격을 변경
    @Update("UPDATE item SET item_price = #{itemPrice} WHERE item_no = #{itemNo}")
    void updatePrice(@Param("itemNo") Long itemNo, @Param("itemPrice") Integer itemPrice);
}
