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
    int save(Order order);

    // 주문 번호로 주문 조회
    Optional<OrderDto.Read> findById(@Param("orderNo") Long orderNo);

    // 모든 주문 조회
    List<OrderDto.OrderList> findAll();
    
    // 모든 주문과 결제 정보 조회
    List<OrderDto.OrderListWithPayment> findAllWithPayments();

    // 주문 정보 업데이트
    int update(Order order);

    // 주문 삭제
    int delete(@Param("orderNo") Long orderNo);
    
    List<Order> findByOrderStatus(@Param("orderStatus") String orderStatus);
}
