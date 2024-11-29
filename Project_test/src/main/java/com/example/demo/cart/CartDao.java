package com.example.demo.cart;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartDao { // 장바구니 관련 데이터 접근 객체 (DAO)
    // 장바구니 저장
    int save(Cart cart); // 장바구니 항목을 데이터베이스에 저장

    // 회원 아이디로 장바구니 조회
    List<CartDto.Read> findAllByUsername(@Param("username") String username); // 특정 사용자의 모든 장바구니 항목 조회

    // 상품 번호와 사용자 이름으로 장바구니 항목 조회
    Optional<CartDto.Read> findByItemNoAndUsername(@Param("itemNo") Long itemNo, @Param("username") String username); // 특정 사용자의 특정 상품 조회

    // 장바구니 상품 수량 업데이트
    int update(Cart cart); // 장바구니 항목의 수량을 업데이트

    // 장바구니 항목 삭제
    int delete(@Param("itemNo") Long itemNo, @Param("username") String username); // 장바구니에서 특정 항목 삭제

	void save(Long itemNo, String username, int detailEa, int price);
}
