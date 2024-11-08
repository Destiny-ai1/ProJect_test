package com.example.demo.order;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface OrderDao {
    // 주문 저장
    @Insert("INSERT INTO orders (order_no, order_date, total_price, order_status, used_point, act_payment, member_username, post_no) VALUES (#{orderNo}, #{orderDate}, #{totalPrice}, #{orderStatus}, #{usedPoint}, #{actPayment}, #{memberUsername}, #{postNo})")
    int save(Order order);

    // 주문 번호로 주문 조회
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Optional<OrderDto.Read> findById(@Param("orderNo") Long orderNo);

    // 모든 주문 조회
    @Select("SELECT * FROM orders")
    List<OrderDto.OrderList> findAll();

    // 주문 정보 업데이트
    @Update("UPDATE orders SET order_status = #{orderStatus} WHERE order_no = #{orderNo}")
    int update(Order order);

    // 주문 삭제
    @Delete("DELETE FROM orders WHERE order_no = #{orderNo}")
    int delete(@Param("orderNo") Long orderNo);
}