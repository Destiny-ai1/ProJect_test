package com.example.demo.card;

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
public class Card { // 카드 엔티티 클래스
    private Long cardNo; // 카드 번호
    private LocalDate exDate; // 카드 유효 기간
    private int cvc; // CVC 번호
    
    private String memberUsername; // 회원 ID (member 테이블과 연관 관계)
}