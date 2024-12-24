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
	// 주문 번호 생성 (시퀀스를 이용)
    @Select("SELECT order_no_seq.NEXTVAL FROM DUAL")
    Long createOrderNo();
	
    // 주문 저장
	void save(OrderDto.Create create);

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
    
    // 결제 후에 주문 상태 업데이트
    @Update("UPDATE ORDERS SET ORDER_STATUS = 'SUCCESS', act_payment = #{actPayment}, USED_POINT = #{usedPoint}, order_date = SYSDATE " +
            "WHERE ORDER_NO = #{orderNo}")
    void updatePaymentInfo(@Param("orderNo") Long orderNo, @Param("actPayment") Long actPayment, 
                           @Param("usedPoint") Long usedPoint);
}
