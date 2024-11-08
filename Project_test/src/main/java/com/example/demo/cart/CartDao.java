package com.example.demo.cart;

import java.util.*;

import org.apache.ibatis.annotations.*;

@Mapper
public interface CartDao { // 장바구니 관련 데이터 접근 객체 (DAO)
    // 장바구니 저장
    @Insert("INSERT INTO cart (item_no, username, cart_ea, cart_price, cart_totalprice) VALUES (#{itemNo}, #{username}, #{cartEa}, #{cartPrice}, #{cartTotalPrice})")
    int save(Cart cart); // 장바구니 항목을 데이터베이스에 저장

    // 회원 아이디로 장바구니 조회
    @Select("SELECT * FROM cart WHERE username = #{username}")
    List<CartDto.Read> findAllByUsername(@Param("username") String username); // 특정 사용자의 모든 장바구니 항목 조회

    // 장바구니 상품 수량 업데이트
    @Update("UPDATE cart SET cart_ea = #{cartEa} WHERE item_no = #{itemNo} AND username = #{username}")
    int update(Cart cart); // 장바구니 항목의 수량을 업데이트

    // 장바구니 항목 삭제
    @Delete("DELETE FROM cart WHERE item_no = #{itemNo} AND username = #{username}")
    int delete(@Param("itemNo") Long itemNo, @Param("username") String username); // 장바구니에서 특정 항목 삭제
}