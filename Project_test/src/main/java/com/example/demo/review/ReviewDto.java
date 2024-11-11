package com.example.demo.review;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

public class ReviewDto { // 리뷰 관련 데이터 전달 객체
    private ReviewDto() {}

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create { // 리뷰 정보를 생성하기 위한 DTO
        @NotNull
        private Long orderNo; // 주문 번호
        @NotEmpty
        private String reviewWriter; // 작성자 ID
        @Min(1)
        @Max(5)
        private int reviewGoodCnt; // 리뷰 별점 (1~5)
        @NotEmpty
        private String username; // 회원 ID

        public Review toEntity() { // DTO를 Review 엔티티로 변환하는 메소드
            return new Review(null, LocalDate.now(), reviewWriter, reviewGoodCnt, username, orderNo);
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Read { // 리뷰 조회를 위한 DTO
        private Long reviewNo; // 리뷰 번호
        private LocalDate reviewWriteTime; // 리뷰 작성 시간
        private String reviewWriter; // 리뷰 작성자
        private int reviewGoodCnt; // 리뷰 별점
        private String username; // 회원 ID
        private Long orderNo; // 주문 번호
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update { // 리뷰 정보를 수정하기 위한 DTO
        @NotNull
        private Long reviewNo; // 리뷰 번호
        @Min(1)
        @Max(5)
        private int reviewGoodCnt; // 수정할 리뷰 별점
        @NotEmpty
        private String reviewWriter; // 수정할 리뷰 작성자

        public Review toEntity() { // DTO를 Review 엔티티로 변환하는 메소드
            return Review.builder().reviewNo(reviewNo).reviewGoodCnt(reviewGoodCnt).reviewWriter(reviewWriter).reviewWriteTime(LocalDate.now()).build();
        }
    }
}