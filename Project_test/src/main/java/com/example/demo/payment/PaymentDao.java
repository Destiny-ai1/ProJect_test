package com.example.demo.payment;

import java.util.*;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PaymentDao { // 결제 관련 데이터 접근 객체 (DAO)
    // 결제 정보 저장
    @Insert("INSERT INTO payment (order_no, pay_method, point_add, card_no) VALUES (#{orderNo}, #{payMethod}, #{pointAdd}, #{cardNo})")
    int save(Payment payment); // 결제 정보를 데이터베이스에 저장

    // 주문 번호로 결제 정보 조회
    @Select("SELECT * FROM payment WHERE order_no = #{orderNo}")
    Optional<PaymentDto.Read> findById(@Param("orderNo") Long orderNo); // 특정 주문 번호로 결제 정보 조회

    // 모든 결제 정보 조회
    @Select("SELECT * FROM payment")
    List<PaymentDto.Read> findAll(); // 모든 결제 정보 조회

    // 결제 정보 업데이트
    @Update("UPDATE payment SET pay_method = #{payMethod}, point_add = #{pointAdd} WHERE order_no = #{orderNo}")
    int update(Payment payment); // 결제 정보 업데이트

    // 결제 정보 삭제
    @Delete("DELETE FROM payment WHERE order_no = #{orderNo}")
    int delete(@Param("orderNo") Long orderNo); // 특정 결제 정보 삭제
}
