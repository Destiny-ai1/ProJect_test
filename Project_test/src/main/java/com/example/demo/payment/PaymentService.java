package com.example.demo.payment;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.FailException;

@Service
public class PaymentService { // 결제 비즈니스 로직을 처리하는 서비스 클래스

    @Autowired
    private PaymentDao paymentDao; // 결제 데이터 접근 객체 (DAO)

    @Transactional
    public void addPayment(PaymentDto.Create dto) { // 결제 정보를 추가하는 메소드
        Payment payment = dto.toEntity(); // DTO를 엔티티로 변환
        paymentDao.save(payment); // 결제 정보 저장
    }

    public PaymentDto.Read getPayment(Long orderNo) { // 특정 주문의 결제 정보를 조회하는 메소드
        return paymentDao.findById(orderNo).orElseThrow(() -> new FailException("결제 정보를 찾을 수 없습니다")); // 주문 번호로 결제 정보 조회
    }

    public List<PaymentDto.Read> getAllPayments() { // 모든 결제 정보를 조회하는 메소드
        return paymentDao.findAll(); // 모든 결제 정보 조회
    }

    @Transactional
    public void updatePayment(PaymentDto.Update dto) { // 결제 정보를 업데이트하는 메소드
        Payment payment = dto.toEntity(); // DTO를 엔티티로 변환
        int updatedRows = paymentDao.update(payment); // 결제 정보 업데이트
        if (updatedRows == 0) { // 업데이트된 항목이 없는 경우 예외 발생
            throw new FailException("결제 정보를 찾을 수 없습니다");
        }
    }

    @Transactional
    public void removePayment(Long orderNo) { // 결제 정보를 삭제하는 메소드
        int deletedRows = paymentDao.delete(orderNo); // 결제 정보 삭제
        if (deletedRows == 0) { // 삭제된 항목이 없는 경우 예외 발생
            throw new FailException("결제 정보를 찾을 수 없습니다");
        }
    }
}