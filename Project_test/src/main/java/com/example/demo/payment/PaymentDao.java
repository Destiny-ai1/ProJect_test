package com.example.demo.payment;

import org.apache.ibatis.annotations.*;

import com.example.demo.payment.PaymentDto.*;

import java.util.Optional;

@Mapper
public interface PaymentDao {

    // 주문 번호로 결제 정보 조회
    Optional<PaymentDto.Read> findById(@Param("orderNo") Long orderNo);

    // 주문 번호로 TID 조회    
    String findTidByOrderNo(@Param("orderNo") Long orderNo);

    // 주문 번호로 TID 업데이트
    int updateTidByOrderNo(@Param("orderNo") Long orderNo, @Param("tid") String tid);

	void update(Read payment);

	int save(Payment payment);

	String findUserIdByOrderNo(Long orderNo);
}
