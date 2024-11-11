package com.example.demo.card;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.*;

public class CardDto { // 카드 관련 데이터 전달 객체
    private CardDto() {}

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create { // 카드 정보를 생성하기 위한 DTO
        @NotNull
        private Long cardNo; // 카드 번호
        @NotNull
        private String exDate; // 카드 유효 기간
        @NotNull
        private int cvc; // CVC 번호
        @NotEmpty
        private String memberUsername; // 회원 ID

        public Card toEntity() { // DTO를 Card 엔티티로 변환하는 메소드
            return new Card(cardNo, LocalDate.parse(exDate), cvc, memberUsername);
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Read { // 카드 조회를 위한 DTO
        private Long cardNo; // 카드 번호
        private String exDate; // 카드 유효 기간
        private int cvc; // CVC 번호
        private String memberUsername; // 회원 ID
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update { // 카드 정보를 수정하기 위한 DTO
        @NotNull
        private Long cardNo; // 카드 번호
        @NotNull
        private String exDate; // 수정할 유효 기간
        @NotNull
        private int cvc; // 수정할 CVC 번호

        public Card toEntity() { // DTO를 Card 엔티티로 변환하는 메소드
            return Card.builder().cardNo(cardNo).exDate(LocalDate.parse(exDate)).cvc(cvc).build();
        }
    }
}