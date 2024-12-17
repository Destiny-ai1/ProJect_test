package com.example.demo.cart;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.item.ItemDto.ItemDeleteDTO;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartDao {
	// 재고 확인 (item_size 테이블에서 해당 상품과 사이즈의 재고 조회)
    @Select("SELECT item_jango FROM item_size WHERE item_no = #{itemNo} AND item_size = #{itemSize}")
    public Integer getStockQuantity(@Param("itemNo") Long itemNo, @Param("itemSize") String itemSize);
    
	// 사용자 장바구니에 상품이 존재하는지 확인 (item_size 포함)
	@Select("SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END " +
	        "FROM cart WHERE username = #{username} AND item_no = #{itemNo} AND item_size = #{itemSize}")
	public Boolean SearchCartByUsernameAndItemNoAndSize(String username, Long itemNo, String itemSize);

    // 장바구니에 상품정보 저장
	@Insert("INSERT INTO cart (item_no, username, cart_ea, cart_price, cart_totalprice, item_size) " +
            "VALUES (#{itemNo}, #{username}, #{cartEa}, #{cartPrice}, #{cartTotalPrice}, #{itemSize})")
    public Integer save(Cart cart);
    
    // 장바구니에 있는 상품 추가시 개수 증가
	public Integer increase(String username, Long itemNo, String itemSize);

    // 구매 개수 변경
	public Integer updateCartEa(@Param("username") String username, 
            @Param("itemNo") Long itemNo, 
            @Param("itemSize") String itemSize,  // itemSize를 추가
            @Param("cartEa") Long requestedEa);


    // 사용자명으로 장바구니 상품 찾기
    public List<CartDto.Read> findByUsername(String username, String imageUrl);

    // 선택된 상품 삭제
    public int deleteCartItems(@Param("items") List<ItemDeleteDTO> items, @Param("username") String username);

    // 주문내용 확인
    public List<CartDto.Read> findByUsernameAndInos(String username, String imageUrl, List<Long> inos);

    // 장바구니 상품 개수 검색 (itemSize 포함)
    @Select("SELECT cart_ea FROM cart WHERE username=#{username} AND item_no=#{itemNo} AND item_size=#{itemSize}")
    public Optional<Integer> findCartEaByUsernameAndItemNoAndSize(String username, Long itemNo, String itemSize);
    
    // 장바구니 총 가격 업데이트
    public Long updateCartTotalPrice(String username, Long itemNo);

}
