package com.example.demo.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.FailException;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao; // 결제 데이터 접근 객체 (DAO)

    @Transactional
    public void setTidForOrderNo(Long orderNo, String tid) { // 주문 번호에 대해 TID 설정하는 메소드
        PaymentDto.Read payment = paymentDao.findById(orderNo)
            .orElseThrow(() -> new FailException("결제 정보를 찾을 수 없습니다."));
        payment.setTid(tid); // TID 설정
        paymentDao.update(payment); // 데이터베이스에 TID 업데이트
    }

    public String getTidByOrderNo(Long orderNo) { // 특정 주문 번호로 TID 조회하는 메소드
        PaymentDto.Read payment = paymentDao.findById(orderNo)
            .orElseThrow(() -> new FailException("결제 정보를 찾을 수 없습니다."));
        return payment.getTid();
    }

    public String getUserIdByOrderNo(Long orderNo) { // 특정 주문 번호로 사용자 ID 조회하는 메소드
        PaymentDto.Read payment = paymentDao.findById(orderNo)
            .orElseThrow(() -> new FailException("결제 정보를 찾을 수 없습니다."));
        return payment.getUsername();
    }
}
