package com.example.demo.order;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderDetailDao {
    // 주문 상세 정보 저장
    int save(OrderDetail orderDetail);

    // 주문 번호로 주문 상세 정보 조회
    @Select("SELECT * FROM orders_detail WHERE order_no = #{orderNo}")
    List<OrderDetail> findByOrderNo(@Param("orderNo") Long orderNo);

    // 주문 상세 정보 업데이트
    @Update("UPDATE orders_detail SET item_name = #{itemName}, image = #{image}, detail_ea = #{detailEa}, price = #{price}, review_written = #{reviewWritten} WHERE order_no = #{orderNo} AND item_no = #{itemNo}")
    int update(OrderDetail orderDetail);

    // 주문 번호로 주문 상세 정보 삭제
    int deleteByOrderNo(Long orderNo);
}
