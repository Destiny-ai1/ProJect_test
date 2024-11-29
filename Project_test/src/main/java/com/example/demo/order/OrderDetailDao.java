package com.example.demo.order;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

@Mapper
public interface OrderDetailDao {
    // 주문 상세 정보 저장
    int save(OrderDetail orderDetail);

    // 주문 번호로 주문 상세 정보 조회
    List<OrderDetail> findByOrderNo(@Param("orderNo") Long orderNo);

    // 주문 상세 정보 업데이트
    int update(OrderDetail orderDetail);

    // 주문 번호로 주문 상세 정보 삭제
    int deleteByOrderNo(Long orderNo);
}
