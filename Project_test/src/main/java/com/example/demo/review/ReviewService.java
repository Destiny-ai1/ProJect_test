package com.example.demo.review;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.FailException;

@Service
public class ReviewService { // 리뷰 비즈니스 로직을 처리하는 서비스 클래스

    @Autowired
    private ReviewDao reviewDao; // 리뷰 데이터 접근 객체 (DAO)

    @Transactional
    public void addReview(ReviewDto.Create dto) { // 리뷰를 추가하는 메소드
        Review review = dto.toEntity(); // DTO를 엔티티로 변환
        reviewDao.save(review); // 리뷰 저장
    }

    public List<ReviewDto.Read> getReviewsByOrderNo(Long orderNo) { // 특정 주문의 리뷰를 조회하는 메소드
        return reviewDao.findByOrderNo(orderNo); // 주문 번호로 리뷰 조회
    }

    @Transactional
    public void updateReview(ReviewDto.Update dto) { // 리뷰 정보를 업데이트하는 메소드
        Review review = dto.toEntity(); // DTO를 엔티티로 변환
        int updatedRows = reviewDao.update(review); // 리뷰 정보 업데이트
        if (updatedRows == 0) { // 업데이트된 항목이 없는 경우 예외 발생
            throw new FailException("리뷰 정보를 찾을 수 없습니다");
        }
    }

    @Transactional
    public void removeReview(Long reviewNo) { // 리뷰를 삭제하는 메소드
        int deletedRows = reviewDao.delete(reviewNo); // 리뷰 삭제
        if (deletedRows == 0) { // 삭제된 항목이 없는 경우 예외 발생
            throw new FailException("리뷰 정보를 찾을 수 없습니다");
        }
    }
}