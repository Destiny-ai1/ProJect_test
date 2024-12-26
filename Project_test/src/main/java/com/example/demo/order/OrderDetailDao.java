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

    // 주문 번호와 사용자명을 Map을 통해 전달받는 방식
    public List<OrderDetail> findAll(String memberUsername, Long orderNo);

    // 주문 상세 정보 업데이트
    int update(OrderDetail orderDetail);

    // 주문 번호로 주문 상세 정보 삭제
    int deleteByOrderNo(Long orderNo);
}
