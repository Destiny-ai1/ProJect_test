package com.example.demo.review;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Component
public class Review { // 리뷰 엔티티 클래스
    private Long reviewNo; // 리뷰 번호
    private LocalDate reviewWriteTime; // 리뷰 작성 시간
    private String reviewWriter; // 리뷰 작성자
    private int reviewGoodCnt; // 리뷰 별점
    private String username; // 회원 ID (member 테이블과 연관 관계)
    private Long orderNo; // 주문 번호 (orders 테이블과 연관 관계)
}